package makza.afonsky.vrcoretest

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val MIN_OPENGL_VERSION = 3.0

    var arFragment: ArFragment? = null
    var lampPostRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?

        maybeEnableArButton()

        ModelRenderable.builder()
            .setSource(this, Uri.parse("rv_lamp_post_3.sfb"))
            .build()
            .thenAccept { renderable: ModelRenderable ->
                lampPostRenderable = renderable
            }
            .exceptionally { throwable: Throwable? ->
                val toast =
                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                null
            }

        //add model to scene
        arFragment!!.setOnTapArPlaneListener { hitresult: HitResult, plane: Plane?, motionevent: MotionEvent? ->
            if (lampPostRenderable == null) {
                return@setOnTapArPlaneListener
            }
            val anchor = hitresult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment!!.arSceneView.scene)
            val lamp =
                TransformableNode(arFragment!!.transformationSystem)
            lamp.setParent(anchorNode)
            lamp.renderable = lampPostRenderable
            lamp.select()
        }


    }



fun maybeEnableArButton() {
    val availability = ArCoreApk.getInstance().checkAvailability(this)
    if (availability.isTransient) {
        // Continue to query availability at 5Hz while compatibility is checked in the background.
        Handler().postDelayed({
            maybeEnableArButton()
        }, 200)
    }
    if (availability.isSupported) {

    } else {
    // The device is unsupported or unknown
    }
}




}