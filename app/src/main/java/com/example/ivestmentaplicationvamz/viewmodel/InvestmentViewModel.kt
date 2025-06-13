
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ivestmentaplicationvamz.ui.component.AnnualEntry
import com.example.ivestmentaplicationvamz.ui.component.ReminderReceiver
import com.example.ivestmentaplicationvamz.ui.component.RepeatInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.sqrt
import java.util.Random
import android.provider.Settings
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.example.ivestmentaplicationvamz.R
import com.example.ivestmentaplicationvamz.data.InvestmentEntity
import com.example.ivestmentaplicationvamz.data.AppDatabase
import kotlinx.coroutines.withContext

class InvestmentViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).investmentDao()



    // —————————————————————————————————————————————————————————
            // Počet rokov
            // —————————————————————————————————————————————————————————
            private val _yearsRaw = MutableStateFlow("")
            val yearsRaw: StateFlow<String> = _yearsRaw

            val yearsInt: StateFlow<Int> = _yearsRaw
                .map { it.toIntOrNull() ?: 0 }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0)


            fun onYearsChange(new: String) {
                _yearsRaw.value = new.filter { it.isDigit() }
            }

            // —————————————————————————————————————————————————————————
            // Frekvencia príspevkov
            // —————————————————————————————————————————————————————————
            private val defaultFrequency = getApplication<Application>().getString(R.string.option_yearly)
            private val _frequencyRaw = MutableStateFlow(defaultFrequency)
            val frequencyRaw: StateFlow<String> = _frequencyRaw
            fun onFrequencyChange(new: String) {
                _frequencyRaw.value = new
            }

            val frequencyPerYear: StateFlow<Int> = _frequencyRaw
                .map { raw ->
                    when (raw) {
                        "Yearly", "Ročné" -> 1
                        "Semi-annual", "Polročné" -> 2
                        "Quarterly", "Kvartálne" -> 4
                        "Monthly", "Mesačné" -> 12
                        "Weekly", "Týždenné" -> 52
                        "Daily", "Denné" -> 365
                        else -> 1
                    }
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, 1)


            // —————————————————————————————————————————————————————————
            // Počiatočný vklad
            // —————————————————————————————————————————————————————————
            private val _startingAmountRaw = MutableStateFlow("")
            val startingAmountRaw: StateFlow<String> = _startingAmountRaw
            fun onStartingAmountChange(new: String) {
                _startingAmountRaw.value = new.filter { it.isDigit() || it == ',' || it == '.' }
            }

            private val startingAmountDouble: StateFlow<Double> = _startingAmountRaw
                .map { raw -> raw.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            val startingAmountFormatted: StateFlow<String> = _startingAmountRaw
                .map { rawInput ->
                    if (rawInput.isNotBlank()) {
                        val amount = rawInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val nf = NumberFormat.getNumberInstance(Locale("sk", "SK")).apply {
                            minimumFractionDigits = 2
                            maximumFractionDigits = 2
                        }
                        nf.format(amount) + " €"
                    } else ""
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")


            // —————————————————————————————————————————————————————————
            // Ročný príspevok
            // —————————————————————————————————————————————————————————
            private val _additionalContributionRaw = MutableStateFlow("")
            val additionalContributionRaw: StateFlow<String> = _additionalContributionRaw
            fun onAdditionalContributionChange(new: String) {
                _additionalContributionRaw.value =
                    new.filter { it.isDigit() || it == ',' || it == '.' }
            }

            private val additionalContributionDouble: StateFlow<Double> = _additionalContributionRaw
                .map { raw -> raw.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)


            // —————————————————————————————————————————————————————————
            // Return %
            // —————————————————————————————————————————————————————————
            private val _returnPercentRaw = MutableStateFlow("")
            val returnPercentRaw: StateFlow<String> = _returnPercentRaw
            fun onReturnPercentChange(new: String) {
                _returnPercentRaw.value = new.filter { it.isDigit() || it == ',' || it == '.' }
            }

            private val returnPercentDouble: StateFlow<Double> = _returnPercentRaw
                .map { raw -> raw.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)


            // —————————————————————————————————————————————————————————
            // Čistá suma príspevkovv
            // —————————————————————————————————————————————————————————
            val totalContributionsNumeric: StateFlow<Double> = combine(
                additionalContributionDouble,
                yearsInt,
                frequencyPerYear
            ) { contrib, yrs, freq ->
                contrib * yrs * freq.toDouble()
            }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            val totalContributionsFormatted: StateFlow<String> = totalContributionsNumeric
                .map { totalContribNumeric ->
                    val nf = NumberFormat.getNumberInstance(Locale("sk", "SK")).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                    }
                    nf.format(totalContribNumeric) + " €"
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")


            // —————————————————————————————————————————————————————————
            // Čistý úrok= budúca hodnota – vklady – počiatočný vklad
            // —————————————————————————————————————————————————————————
            val totalInterestNumeric: StateFlow<Double> = combine(
                startingAmountDouble,
                additionalContributionDouble,
                yearsInt,
                frequencyPerYear,
                returnPercentDouble
            ) { start, contrib, yrs, freq, rPercent ->
                val r = rPercent / 100.0
                val n = freq
                val t = yrs
                val periods = n * t

                if (t == 0 || (start == 0.0 && contrib == 0.0)) {
                    0.0
                } else {
                    val periodicRate = if (n > 0) r / n else 0.0
                    val growthFactor = (1 + periodicRate).pow(periods.toDouble())
                    val fvStart = start * growthFactor
                    val fvContrib = if (periodicRate == 0.0) {
                        contrib * periods.toDouble()
                    } else {
                        contrib * ((growthFactor - 1) / periodicRate)
                    }
                    val endBalance = fvStart + fvContrib
                    val totalContribNumeric = contrib * periods.toDouble()
                    val totalInterest = endBalance - start - totalContribNumeric
                    totalInterest
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            val totalInterestFormatted: StateFlow<String> = totalInterestNumeric
                .map { totalInterest ->
                    val nf = NumberFormat.getNumberInstance(Locale("sk", "SK")).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                    }
                    nf.format(totalInterest) + " €"
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")


            // —————————————————————————————————————————————————————————
            // Konečna hodnota = t. j. start + totalContributionsNumeric + totalInterestNumeric
            // —————————————————————————————————————————————————————————
            val endBalanceNumeric: StateFlow<Double> = combine(
                startingAmountDouble,
                totalContributionsNumeric,
                totalInterestNumeric
            ) { start, totalContrib, totalInterest ->
                start + totalContrib + totalInterest
            }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            val endBalanceFormatted: StateFlow<String> = endBalanceNumeric
                .map { endValue ->
                    val nf = NumberFormat.getNumberInstance(Locale("sk", "SK")).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                    }
                    nf.format(endValue) + " €"
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val annualSchedule: StateFlow<List<AnnualEntry>> = combine(
                startingAmountDouble,
                additionalContributionDouble,
                returnPercentDouble,
                yearsInt,
                frequencyPerYear
            ) { start, contribPerPeriod, rPercent, yrs, freqPerYear ->
                val r = rPercent / 100.0
                val n = freqPerYear
                var currentBalance = start
                val list = mutableListOf<AnnualEntry>()
                for (yearIndex in 1..yrs) {
                    val depositYear = contribPerPeriod * n.toDouble()
                    val periodicRate = if (n > 0) r / n else 0.0
                    val growthFactor = (1 + periodicRate).pow(n.toDouble())
                    val fvStart = currentBalance * growthFactor
                    val fvContrib = if (periodicRate == 0.0) {
                        depositYear
                    } else {
                        contribPerPeriod * ((growthFactor - 1) / periodicRate)
                    }
                    val endingBalance = fvStart + fvContrib
                    val interestYear = endingBalance - currentBalance - depositYear
                    list.add(
                        AnnualEntry(
                            year = yearIndex,
                            deposit = depositYear,
                            interest = interestYear,
                            endingBalance = endingBalance
                        )
                    )
                    currentBalance = endingBalance
                }
                list.toList()
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

            // ───────────────────────────────────────────────
            // Inflácia
            // ───────────────────────────────────────────────
            private val _inflationRaw = MutableStateFlow("")
            val inflationRaw: StateFlow<String> = _inflationRaw
            fun onInflationChange(new: String) {
                _inflationRaw.value = new.filter { it.isDigit() || it == ',' || it == '.' }
            }

            private val inflationPercentDouble: StateFlow<Double> = _inflationRaw
                .map { it.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            // ───────────────────────────────────────────────
            // Skutočná miera = (1 + nominal) / (1 + inflation) − 1
            // ───────────────────────────────────────────────
            private val realRateDouble: StateFlow<Double> = combine(
                returnPercentDouble,
                inflationPercentDouble
            ) { nominal, inflation ->
                val rn = nominal / 100.0
                val ri = inflation / 100.0
                if (ri == -1.0) 0.0
                else (1 + rn) / (1 + ri) - 1
            }
                .distinctUntilChanged()
                .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            // ───────────────────────────────────────────────
            // Celkovo investované z infláciou
            // ───────────────────────────────────────────────
            val totalInterestWInflationFormatted: StateFlow<String> = combine(
                startingAmountDouble,
                additionalContributionDouble,
                yearsInt,
                frequencyPerYear,
                realRateDouble
            ) { start, contrib, yrs, freq, realR ->
                val r = realR
                val n = freq
                val t = yrs
                val periods = n * t
                if (t == 0 || (start == 0.0 && contrib == 0.0)) {
                    "0,00 €"
                } else {
                    val periodicRate = if (n > 0) r / n else 0.0
                    val growthFactor = (1 + periodicRate).pow(periods.toDouble())
                    val fvStart = start * growthFactor
                    val fvContrib =
                        if (periodicRate == 0.0) contrib * periods else contrib * ((growthFactor - 1) / periodicRate)
                    val endBalance = fvStart + fvContrib
                    val totalContrib = contrib * periods
                    val interestReal = endBalance - start - totalContrib
                    NumberFormat.getNumberInstance(Locale("sk", "SK")).run {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 2
                        format(interestReal) + " €"
                    }
                }
            }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            // Real end po inflácii:
            private val realEndBalanceNumeric: StateFlow<Double> = combine(
                endBalanceNumeric,
                inflationPercentDouble,
                yearsInt
            ) { nominalEnd, infPct, yrs ->
                val i = infPct / 100.0
                if (yrs <= 0) nominalEnd
                else nominalEnd / (1 + i).pow(yrs.toDouble())
            }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            // Celková strata na inflácii
            private val inflationLossTotalNumeric: StateFlow<Double> = combine(
                endBalanceNumeric,
                realEndBalanceNumeric
            ) { nominalEnd, realEnd ->
                nominalEnd - realEnd
            }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            // Ročná strata (priemer) -> všetky roky
            private val inflationLossYearlyNumeric: StateFlow<Double> = combine(
                inflationLossTotalNumeric,
                yearsInt
            ) { lossTotal, yrs ->
                if (yrs <= 0) 0.0 else lossTotal / yrs.toDouble()
            }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            // formatovanie
            val inflationRateFormatted: StateFlow<String> = _inflationRaw
                .map { raw -> if (raw.isBlank()) "" else raw.trim() + " %" }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            private fun formatCurrency(amount: Double, decimals: Int = 0): String {
                val nf = NumberFormat.getNumberInstance(Locale("sk", "SK")).apply {
                    minimumFractionDigits = decimals
                    maximumFractionDigits = decimals
                }
                return nf.format(amount) + " €"
            }


            val inflationLossYearlyFormatted: StateFlow<String> = inflationLossYearlyNumeric
                .map { formatCurrency(it, 0) }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val inflationLossTotalFormatted: StateFlow<String> = inflationLossTotalNumeric
                .map { formatCurrency(it, 0) }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val realEndBalanceFormatted: StateFlow<String> = realEndBalanceNumeric
                .map { formatCurrency(it, 0) }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            private val _showInflation = MutableStateFlow(false)
            val showInflation: StateFlow<Boolean> = _showInflation
            fun onInflationToggle(enabled: Boolean) {
                _showInflation.value = enabled
            }



            // ─────────────────────────────────────────────────
            // daň z úroku posledného roka
            // ─────────────────────────────────────────────────
            private val _taxPercentRaw = MutableStateFlow("")
            val taxPercentRaw: StateFlow<String> = _taxPercentRaw
            fun onTaxPercentChange(new: String) {
                _taxPercentRaw.value = new.filter { it.isDigit() || it == ',' || it == '.' }
            }

            private val taxPercentDouble: StateFlow<Double> = _taxPercentRaw
                .map { it.replace(",", ".").toDoubleOrNull() ?: 0.0 }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)


            private val taxAmountNumeric: StateFlow<Double> = combine(
                additionalContributionDouble,
                frequencyPerYear,
                returnPercentDouble,
                taxPercentDouble
            ) { contribPerPeriod, n, rPercent, taxPct ->
                val r = rPercent / 100.0
                val taxRate = taxPct / 100.0

                //celkove priespevky
                val totalContribLastYear = contribPerPeriod * n.toDouble()

                // hodnota týchto príspevkov za 1 rok:
                val gainLastYear = if (r == 0.0) {
                    0.0
                } else {
                    val periodicRate = r / n.toDouble()
                    val fv = contribPerPeriod *
                            ((1 + periodicRate).pow(n.toDouble()) - 1) /
                            periodicRate
                    // čistý zisk
                    fv - totalContribLastYear
                }

                //daň zo zisku zisku
                gainLastYear * taxRate
            }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            val taxAmountFormatted: StateFlow<String> = taxAmountNumeric
                .map { formatCurrency(it, 2) }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val interestAfterTaxFormatted: StateFlow<String> = combine(
                totalInterestNumeric,
                taxAmountNumeric
            ) { totalInt, taxAmt ->
                formatCurrency(totalInt - taxAmt, 2)
            }.stateIn(viewModelScope, SharingStarted.Lazily, "")

            private val _showTax = MutableStateFlow(false)
            val showTax: StateFlow<Boolean> = _showTax
            fun onTaxToggle(enabled: Boolean) {
                _showTax.value = enabled
            }

            // ročná priemerná daň = daň z posledného roka / počet rokov
            private val taxYearlyNumeric: StateFlow<Double> = combine(
                taxAmountNumeric,
                yearsInt
            ) { taxAmt, yrs ->
                if (yrs > 0) taxAmt / yrs.toDouble() else 0.0
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = 0.0
            )

            val taxYearlyFormatted: StateFlow<String> = taxYearlyNumeric
                .map { formatCurrency(it, 0) }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            //čistý zisk príspevkov posledného roka
            private val lastYearGainNumeric: StateFlow<Double> = combine(
                additionalContributionDouble,
                frequencyPerYear,
                returnPercentDouble
            ) { contribPerPeriod, freq, rPercent ->
                val r = rPercent / 100.0
                val totalContribLastYear = contribPerPeriod * freq.toDouble()
                if (r == 0.0) {
                    0.0
                } else {
                    val periodicRate = r / freq.toDouble()
                    val fv =
                        contribPerPeriod * ((1 + periodicRate).pow(freq.toDouble()) - 1) / periodicRate
                    fv - totalContribLastYear
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

            val lastYearGainFormatted: StateFlow<String> = lastYearGainNumeric
                .map { formatCurrency(it, 0) }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            // ─────────────────────────────────────────────────────────────────────
            // Monte Carlo: simulujeme konečný zostatok endBalance
            // ─────────────────────────────────────────────────────────────────────
            private val _monteResults = MutableStateFlow<List<Double>>(emptyList())

            val _monteFinished = MutableStateFlow(false)
            val monteFinished: StateFlow<Boolean> = _monteFinished.asStateFlow()

            suspend fun runMonteCarlo(sims: Int = 10_000, volPct: Double = 0.15) {
                viewModelScope.launch(Dispatchers.Default) {
                    val start = startingAmountDouble.value
                    val contribPerPd = additionalContributionDouble.value
                    val yrs = yearsInt.value
                    val freq = frequencyPerYear.value
                    val meanR = returnPercentDouble.value / 100.0
                    val vol = volPct

                    val results = List(sims) {
                        simulateOnce(start, contribPerPd, yrs, freq, meanR, vol)
                    }
                    _monteResults.value = results
                    _monteFinished.value = true
                }
            }

            private val rng = Random()

            private fun simulateOnce(
                start: Double,
                contribPerPd: Double,
                yrs: Int,
                freq: Int,
                meanR: Double,
                vol: Double
            ): Double {
                var bal = start
                repeat(yrs) {
                    repeat(freq) {
                        val mi = meanR / freq
                        val sigma = vol / sqrt(freq.toDouble())

                        val rate = rng.nextGaussian() * sigma + mi

                        bal = bal * (1 + rate) + contribPerPd
                    }
                }
                return bal
            }

            // ─────────────────────────────────────────────────────────────────────
            // Štatistiky z výsledkov simulácií
            // ─────────────────────────────────────────────────────────────────────

            private fun percentile(sorted: List<Double>, p: Double): Double {
                if (sorted.isEmpty()) return 0.0
                val rank = p / 100.0 * (sorted.size - 1)
                val i = rank.toInt()
                val frac = rank - i
                return if (i + 1 < sorted.size)
                    sorted[i] * (1 - frac) + sorted[i + 1] * frac
                else sorted[i]
            }

            // MIN, MAX
            val monteMinFormatted: StateFlow<String> = _monteResults
                .map { it.minOrNull()?.let { formatCurrency(it, 0) } ?: "" }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val monteMaxFormatted: StateFlow<String> = _monteResults
                .map { it.maxOrNull()?.let { formatCurrency(it, 0) } ?: "" }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            // Percentily 10,25,50,75
            val monteP10Formatted: StateFlow<String> = _monteResults
                .map { list ->
                    val s = list.sorted()
                    formatCurrency(percentile(s, 10.0), 0)
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val monteP25Formatted: StateFlow<String> = _monteResults
                .map { list ->
                    val s = list.sorted()
                    formatCurrency(percentile(s, 25.0), 0)
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val monteP50Formatted: StateFlow<String> = _monteResults
                .map { list ->
                    val s = list.sorted()
                    formatCurrency(percentile(s, 50.0), 0)
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val monteP90Formatted: StateFlow<String> = _monteResults
                .map { list ->
                    val s = list.sorted()
                    formatCurrency(percentile(s, 75.0), 0)
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            // MEDIAN
            val monteMedianFormatted = monteP50Formatted

            // priemer rocny navrat
            val monteAvgAnnReturnFormatted: StateFlow<String> = combine(
                _monteResults,
                startingAmountDouble,
                totalContributionsNumeric,
                yearsInt
            ) { list, start, totalContrib, yrs ->
                if (list.isEmpty() || yrs <= 0) return@combine ""
                val meanEnd = list.average()
                val avgReturn = (meanEnd - start - totalContrib) / yrs.toDouble()
                formatCurrency(avgReturn, 0)
            }.stateIn(viewModelScope, SharingStarted.Lazily, "")

            private val monteAnnualReturns: StateFlow<List<Double>> = combine(
                _monteResults,
                startingAmountDouble,
                totalContributionsNumeric,
                yearsInt
            ) { results, start, totalContrib, yrs ->
                if (yrs <= 0) return@combine emptyList()
                results.map { finalBal ->
                    (finalBal - start - totalContrib) / yrs.toDouble()
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

            // 2) odchýlka
            val monteStdDevFormatted: StateFlow<String> =
                monteAnnualReturns
                    .map { list ->
                        if (list.size < 2) return@map ""
                        val mean = list.average()
                        val variance = list.sumOf { (it - mean).pow(2) } / (list.size - 1)
                        formatCurrency(sqrt(variance), 0)
                    }
                    .stateIn(viewModelScope, SharingStarted.Lazily, "")

            // suma simulacii
            val monteCountFormatted: StateFlow<String> = _monteResults
                .map { it.size.toString() }
                .stateIn(viewModelScope, SharingStarted.Lazily, "0")

            //pravdepodobnosť úspechu
            val successProbFormatted: StateFlow<String> = combine(
                _monteResults,
                startingAmountDouble,
                totalContributionsNumeric
            ) { results, start, totalContrib ->
                if (results.isEmpty()) return@combine ""
                val prem = start + totalContrib
                val succCount = results.count { it >= prem }
                "${(succCount * 100.0 / results.size).toInt()} %"
            }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            val lossProbFormatted: StateFlow<String> = successProbFormatted
                .map { raw ->
                    raw.removeSuffix(" %").toIntOrNull()?.let { (100 - it).toString() + " %" } ?: ""
                }
                .stateIn(viewModelScope, SharingStarted.Lazily, "")

            private val _showMonteCarlo = MutableStateFlow(false)
            val showMonteCarlo: StateFlow<Boolean> = _showMonteCarlo

            fun onMonteCarloToggle(enabled: Boolean) {
                _showMonteCarlo.value = enabled
            }


    //naplánovanie pripomineky

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleReminder(
        dateTime: LocalDateTime,
        interval: RepeatInterval, // TODO: interval (chcel som aby sa naplánovali dalšie odosielania ale to som už nedokončil)
        principal: String,
        contribution: String,
        years: String,
        rate: String,
        frequencyLabel: String
    ) {

        val ctx = getApplication<Application>()

        val channelId = "invest_reminders"
        val channelName = "Pripomienky investovania"
        val notificationManager = ctx.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ctx, ReminderReceiver::class.java).apply {
            putExtra("EXTRA_PRINCIPAL", principal)
            putExtra("EXTRA_CONTRIBUTION", contribution)
            putExtra("EXTRA_YEARS", years)
            putExtra("EXTRA_RATE", rate)
            putExtra("EXTRA_FREQUENCY_LABEL", frequencyLabel)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            ctx, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerMs = dateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms()) {
                am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMs,
                    pendingIntent
                )
            } else {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ctx.startActivity(intent)
            }
        } else {
            am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMs,
                pendingIntent
            )
        }

    }

    //práca z databázov

    suspend fun saveToDbSuspend(entity: InvestmentEntity): Long {
        return dao.insert(entity)
    }

    val allInvestments: StateFlow<List<InvestmentEntity>> =
        dao.getAll()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteInvestment(entity: InvestmentEntity, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val count = dao.delete(entity)
            withContext(Dispatchers.Main) {
                onComplete(count)
            }
        }
    }

    fun loadIntoInputs(entity: InvestmentEntity) {
        _startingAmountRaw.value = entity.principal.toString()
        _additionalContributionRaw.value = entity.contribution.toString()
        _yearsRaw.value = entity.years.toString()
        _returnPercentRaw.value = entity.ratePercent.toString()
        _frequencyRaw.value = entity.frequency

        _showMonteCarlo.value = entity.simulationEnabled
        _showInflation.value = entity.inflationEnabled
        _inflationRaw.value = entity.inflationRate?.toString() ?: ""
        _showTax.value = entity.taxEnabled
        _taxPercentRaw.value = entity.taxRate?.toString() ?: ""
    }
}




