import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

fun main() = application {
     Window(
        onCloseRequest = ::exitApplication,
        title = "SportsLineKMP",
    ) {
        App()
    }

    crearArchivosXml()
}