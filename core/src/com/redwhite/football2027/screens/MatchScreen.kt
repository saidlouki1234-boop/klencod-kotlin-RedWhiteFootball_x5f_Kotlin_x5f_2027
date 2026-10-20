
package com.redwhite.football2027.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.redwhite.football2027.GameMain
import com.redwhite.football2027.model.MatchModel

class MatchScreen(private val game: GameMain): Screen {
    private val model = MatchModel()
    private val shape = ShapeRenderer()
    private var joystick = Vector2()
    private var shootCharge = 0f
    private var isShooting = false

    override fun show() {
        Gdx.input.inputProcessor = object: com.badlogic.gdx.InputAdapter(){
            override fun touchDragged(x:Int,y:Int,p:Int): Boolean {
                if (x<540) { // left half joystick
                    joystick.x = (x-200)/150f
                    joystick.y = -(y-1400)/150f
                    joystick.clamp(-1f,1f)
                }
                return true
            }
            override fun touchUp(x:Int,y:Int,p:Int,b:Int): Boolean { joystick.setZero(); return true }
        }
    }

    override fun render(delta: Float) {
        model.update(delta)

        // Input -> player
        val user = model.teamA.firstOrNull { it.isUser }
        user?.let{
            it.vx = joystick.x*300f
            it.vy = joystick.y*300f
            // auto dribble
            if (dist(it.x,it.y,model.ball.x,model.ball.y)<60f){
                model.ball.x += it.vx*delta*0.6f
                model.ball.y += it.vy*delta*0.6f
            }
        }

        // shoot charge with right side touch
        if (Gdx.input.isTouched && Gdx.input.x>540) {
            isShooting=true; shootCharge+=delta
        } else if (isShooting) {
            // release shoot
            val power = (500f + shootCharge.coerceAtMost(1.2f)*1200f)
            model.ball.vx = joystick.x*100f
            model.ball.vy = if (model.ball.y<960) power else -power
            isShooting=false; shootCharge=0f
        }

        val goal = model.checkGoal()

        Gdx.gl.glClearColor(0.18f,0.55f,0.18f,1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shape.begin(ShapeRenderer.ShapeType.Filled)

        // field lines
        shape.color = Color.WHITE; shape.rectLine(20f,20f,1060f,20f,3f); shape.rectLine(20f,1900f,1060f,1900f,3f)
        // goals
        shape.color = Color.valueOf("D10A1F"); shape.rect(400f,10f,280f,25f); shape.rect(400f,1885f,280f,25f)

        // players TeamA red
        shape.color = Color.valueOf("D10A1F")
        model.teamA.forEach{ shape.circle(it.x,it.y,22f) }
        // TeamB blue
        shape.color = Color.valueOf("0A2A9A")
        model.teamB.forEach{ shape.circle(it.x,it.y,22f) }

        // ball
        shape.color = Color.WHITE; shape.circle(model.ball.x, model.ball.y, 14f)
        shape.color = Color.BLACK; shape.circle(model.ball.x, model.ball.y, 14f, 8) // outline trick
        shape.end()

        game.batch.begin()
        game.font.color = Color.WHITE
        game.font.draw(game.batch, "${model.scoreA} - ${model.scoreB}  ${model.timeSec.toInt()/60}:${(model.timeSec.toInt()%60).toString().padStart(2,'0')}", 440f, 1850f)
        game.font.draw(game.batch, if(isShooting) "شحن التسديد: ${(shootCharge*100).toInt()}%" else "المس اليسار للحركة - اليمين للتسديد", 100f, 120f)
        if (goal!=null) game.font.draw(game.batch, "هدف! GOAL!", 460f, 1000f)
        if (!model.isPlaying) game.font.draw(game.batch, "انتهت المباراة - Final: ${model.scoreA}-${model.scoreB}", 320f, 1000f)
        game.batch.end()
    }

    private fun dist(ax:Float,ay:Float,bx:Float,by:Float)= kotlin.math.sqrt((ax-bx)*(ax-bx)+(ay-by)*(ay-by))
    override fun resize(w:Int,h:Int){}; override fun pause(){}; override fun resume(){}; override fun hide(){}; override fun dispose(){ shape.dispose() }
}
