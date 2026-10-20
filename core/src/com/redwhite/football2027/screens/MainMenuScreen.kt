
package com.redwhite.football2027.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.redwhite.football2027.GameMain
import com.redwhite.football2027.model.TeamsRepo

class MainMenuScreen(private val game: GameMain): Screen {
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        Gdx.input.inputProcessor = stage
        val title = Label("RedWhiteFootball 2027\nالنادي الإفريقي - باب الجديد", skin)
        title.setPosition(50f, 1600f)
        title.color = Color.valueOf("D10A1F")
        stage.addActor(title)

        val btnPlay = TextButton("ابدأ المباراة - PLAY", skin)
        btnPlay.setPosition(350f, 900f); btnPlay.setSize(380f,120f)
        btnPlay.addListener(object: ChangeListener(){ override fun changed(e: ChangeEvent?, a: Actor?) { game.setScreen(MatchScreen(game)) } })
        stage.addActor(btnPlay)

        val lbl = Label("الفرق: " + TeamsRepo.teams.joinToString(", ") { it.nameAr }, skin)
        lbl.setPosition(30f, 600f); lbl.setWrap(true); lbl.width=1000f
        stage.addActor(lbl)
    }
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(1f,1f,1f,1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta); stage.draw()
    }
    override fun resize(w:Int,h:Int){ stage.viewport.update(w,h,true) }
    override fun pause(){}; override fun resume(){}; override fun hide(){}; override fun dispose(){ stage.dispose(); skin.dispose() }
}
