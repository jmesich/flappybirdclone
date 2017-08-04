package com.james.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	BitmapFont font;
	//textures
	Texture background;
	Texture[] birds;
	Texture topBlock;
	Texture bottomBlock;
	//shapes
	Rectangle[] topRect;
	Rectangle[] bottomRect;
	Circle birdCircle;
	//ints and floats
	//y coord of the bird
	float birdY= 0;
	//score of user
	int score=0;
	//tracks which tube is the most recently passed
	int scoring=0;
	//speed
	float velocity=0;
	//gravity
	float gravity=2.5f;
	//game state determines if started,game over or playing
	int gameState=0;
	//gap between pipes
	float gap=400;
	//rand number for pipe placement
	Random random;
	//speed of game
	float tubeSpeed=4;
	//self explanitory
	int numberOfTubes=4;
	// tracks x coord of the tubes
	float[] tubeX=new float[numberOfTubes];
	//tracks the size of the tube offset
	float[] tubeOffset=new float[numberOfTubes];
	//self explanitory
	float distanceBetweenTubes;
	//screen dimensions
	float screenHeight;
	float screenWidth;
	//tube dimensions
	float tubeHeight;
	float tubeWidth;
	//bird dimensions
	float birdHeight;
	float birdWidth;
	@Override
	public void create () {
		//creates necessary objects
		batch = new SpriteBatch();
		//shapes
		topRect = new Rectangle[numberOfTubes];
		bottomRect = new Rectangle[numberOfTubes];
		birdCircle = new Circle();
		//textures
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topBlock = new Texture("toptube.png");
		bottomBlock = new Texture("bottompipe.png");
		//bird dimensions
		birdHeight=birds[0].getHeight();
		birdWidth=birds[0].getWidth();
		//tube dimensions
		tubeHeight=topBlock.getHeight();
		tubeWidth=topBlock.getWidth();
		//screen size
		screenHeight=Gdx.graphics.getHeight();
		screenWidth=Gdx.graphics.getWidth();
		//scoring font
		font = new BitmapFont();
		font.setColor(Color.OLIVE);
		font.getData().setScale(10);
		//random number gen
		random = new Random();
		distanceBetweenTubes = screenWidth * (3 / 4);
		//self explanatory
		startGame();

	}
	public void startGame(){
		birdY=screenHeight/2-(birdHeight/2);
		for(int i=0;i<numberOfTubes;i++){
			tubeOffset[i] = (random.nextFloat()-0.5f)*(screenHeight-gap-200);
			tubeX[i]=screenWidth/2-tubeWidth/2+screenWidth+i*distanceBetweenTubes;
			topRect[i]= new Rectangle();
			bottomRect[i]= new Rectangle();
		}
	}
	//game over screen, hey if you made it here you might have lost the game but it means i did this right so thats cool
	public void gameOverState(){
		Texture gameOver = new Texture("gameover.png");
		batch.begin();
		batch.draw(gameOver,screenWidth/2-gameOver.getWidth()/2,screenHeight/2-gameOver.getHeight()/2);
		if(Gdx.input.justTouched()){
			//changes game state back to playing
			gameState=1;
			startGame();
			//resets game
			score=0;
			scoring=0;
			velocity=0;
		}
		batch.end();
	}
	//gameplay logic
	public void playing(){
		if (tubeX[scoring]<screenWidth/2){
			score++;
			if(scoring<numberOfTubes-1){
				scoring++;
			}
			else{
				scoring=0;
			}
		}

		for(int i =0;i<numberOfTubes;i++) {
			if(tubeX[i]<-tubeWidth){
				tubeX[i]+= numberOfTubes * distanceBetweenTubes;
			}
			else {
				tubeX[i] -= tubeSpeed;

			}
			//draws the pipes
			batch.draw(topBlock, tubeX[i], screenHeight / 2 + gap / 2 + tubeOffset[i]);
			batch.draw(bottomBlock, tubeX[i], screenHeight / 2 - gap / 2 - tubeHeight + tubeOffset[i]);
			//builds the shapes
			topRect[i] = new Rectangle(tubeX[i],screenHeight / 2 + gap / 2 + tubeOffset[i],tubeWidth,tubeHeight);
			bottomRect[i]=new Rectangle(tubeX[i],screenHeight / 2 - gap / 2 - tubeHeight + tubeOffset[i],tubeWidth,tubeHeight);
		}
		//makes sure the bird is still on the screen
		if(birdY>0) {
			//increase velocity
			velocity += gravity;
			birdY -= velocity;
		}
		else{
			gameState=2;
		}
		if(Gdx.input.justTouched()){
			velocity=-30;
		}
	}
	//checks collision, "dont touch me rectangle... youWeird" said the circle
	public void collision(){
		for(int i =0;i<numberOfTubes;i++){
			if(Intersector.overlaps(birdCircle,topRect[i])||Intersector.overlaps(birdCircle,bottomRect[i])){
				gameState=2;
			}
		}
	}
	//makes it look like its flapping its little wings, this is expert level animation
	public void flappy(){
		int flapState=1;
		if(flapState==1){
			flapState=0;
		}
		else{
			flapState=1;
		}
		batch.draw(birds[flapState],screenWidth/2-(birdWidth/2),birdY);
	}
	//gotta have a clean main method for the gender of choice #equality
	//this is where it will point to the proper methods that control how it works
	@Override
	public void render() {
		batch.begin();
		//draws background
		batch.draw(background,0,0,screenWidth,screenHeight);
		/*gamestate
			0 means not started
			1 means playing
			2 means game over
		*/
		if(gameState!=1){
			playing();
		}
		//changes game mode to 1, which in case ya forget, it means GAME ON!!1! wow lit
		else if(gameState==0){
			if(Gdx.input.justTouched()){
				gameState=1;
			}
		}
		//restarts game after loss
		else if(gameState==2){
			gameOverState();
		}
		//look at its wings go
		flappy();
		//draws the bird

		batch.end();
		birdCircle.set(screenWidth/2,birdY + birdHeight/2,birdWidth/2);
		//draws the score
		font.draw(batch,Integer.toString(score),100,200);
		//checks if the bird hits something
		collision();
	}
}
