package id.adeyds.samplematerialdaterangepicker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val selectedDates = mutableStateListOf<Long>()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val (firstDate, lastDate) = getTimeInMillisForFirstAndLastDateOfMonth()
        setContent {
            val startDate = remember { mutableStateOf(firstDate) }
            val endDate = remember { mutableStateOf(lastDate) }

            DateRangePickerExample(dateFormat, startDate, endDate) {
                showDateRangePicker(
                    startDate = startDate.value,
                    endDate = endDate.value,
                    startDateChanges = { newStartDate ->
                        startDate.value = newStartDate
                    },
                    endDateChanges = { newEndDate ->
                        endDate.value = newEndDate
                    }
                )
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getTimeInMillisForFirstAndLastDateOfMonth(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDateOfMonthMillis = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDateOfMonthMillis = calendar.timeInMillis

        return Pair(firstDateOfMonthMillis, lastDateOfMonthMillis)
    }

    private fun showDateRangePicker(
        startDate: Long,
        endDate: Long,
        startDateChanges: (Long) -> Unit,
        endDateChanges: (Long) -> Unit,
    ) {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val constraintsBuilder = CalendarConstraints.Builder()
        builder.setCalendarConstraints(constraintsBuilder.build())
        builder.setTitleText("Select dates")
        val prefill = androidx.core.util.Pair(startDate, endDate)
        builder.setSelection(prefill)

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection ->
            val startDateMillis = selection.first
            val endDateMillis = selection.second
            if (startDateMillis != null && endDateMillis != null) {
                startDateChanges(startDateMillis)
                endDateChanges(endDateMillis)
                selectedDates.apply {
                    clear()
                    add(selection.first)
                    add(selection.second)
                }
                showToast(
                    "Selected dates: ${
                        dateFormat.format(startDateMillis)
                    } - ${
                        dateFormat.format(endDateMillis)
                    }"
                )
            }
        }
        picker.show(supportFragmentManager, picker.toString())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerExample(
    dateFormat: SimpleDateFormat,
    startDate: MutableState<Long>,
    endDate: MutableState<Long>,
    buttonAction: () -> Unit
) {
    Column {
        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name)) }
        )
        Button(
            onClick = buttonAction,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Select Date Range")
        }
        Text(
            text = "Selected Dates: ${
                "${dateFormat.format(startDate.value)} - ${dateFormat.format(endDate.value)}"
            }",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}