import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.teamdev.jxbrowser.browser.Browser
import com.teamdev.jxbrowser.browser.callback.AlertCallback
import com.teamdev.jxbrowser.browser.callback.ConfirmCallback
import com.teamdev.jxbrowser.engine.Engine
import com.teamdev.jxbrowser.engine.RenderingMode.OFF_SCREEN
import com.teamdev.jxbrowser.view.swing.BrowserView

fun main() = application {
    val engine = Engine.newInstance(OFF_SCREEN)
    val browser = engine.newBrowser()
    val url = remember { mutableStateOf("https://www.google.com") }

    Window(
        onCloseRequest = ::exitApplication,
        title = "JxBrowser Compose Example",
        state = WindowState(size = DpSize(width = 1200.dp, height = 800.dp))
    ) {
        Column {
            AddressBar(browser, url)
            SwingPanel(
                //modifier = Modifier.fillMaxSize(),
                //modifier = Modifier.width(600.dp).height(600.dp),
                modifier = Modifier.width(500.dp).height(600.dp),
                factory = {
                    BrowserView.newInstance(browser)
                }
            )
        }
        showDialog(browser)
    }

    browser.navigation().loadUrl("https://www.google.com")
    browser.devTools().show()
}

@Composable
private fun showDialog(browser: Browser) {
    val openAlertDialog = remember { mutableStateOf(false) }
    val openConfirmDialog = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val actionOk = remember { mutableStateOf({}) }
    val actionCancel = remember { mutableStateOf({}) }

    browser.set(AlertCallback::class.java, AlertCallback { params, tell ->
        title.value = params.title()
        message.value = params.message()
        actionOk.value = { tell.ok() }
        openAlertDialog.value = true
    })

    browser.set(ConfirmCallback::class.java, ConfirmCallback { params, tell ->
        title.value = params.title()
        message.value = params.message()
        actionOk.value = { tell.ok() }
        actionCancel.value = { tell.cancel() }
        openConfirmDialog.value = true
    })

    if (openAlertDialog.value) {
        ComposeAlertDialog(title.value, message.value, actionOk.value, openAlertDialog)
    }
    if (openConfirmDialog.value) {
        ComposeConfirmDialog(title.value, message.value, actionOk.value, actionCancel.value, openConfirmDialog)
    }
}

@Composable
private fun AddressBar(browser: Browser, url: MutableState<String>) {
    Row {
        BasicTextField(
            value = url.value,
            onValueChange = {
                url.value = it
            },
            modifier = Modifier.weight(1f).padding(start = 10.dp, top = 10.dp),
            singleLine = true
        )
        Spacer(Modifier.width(10.dp))
        Button(
            modifier = Modifier.height(40.dp),
            onClick = { browser.navigation().loadUrl(url.value) }
        ) {
            Text(text = "Go!")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ComposeAlertDialog(
    title: String,
    message: String,
    actionOk: () -> Unit,
    openDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            actionOk()
            openDialog.value = false
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        actionOk()
                        openDialog.value = false
                    }
                ) {
                    Text("Ok")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ComposeConfirmDialog(
    title: String,
    message: String,
    actionOk: () -> Unit,
    actionCancel: () -> Unit,
    openDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            actionCancel()
            openDialog.value = false
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        actionOk()
                        openDialog.value = false
                    }
                ) {
                    Text("Ok")
                }
                Button(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    onClick = {
                        actionCancel()
                        openDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}
