package application;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import controller.GameOverController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransitionBuilder;
import javafx.animation.SequentialTransitionBuilder;
import javafx.animation.TimelineBuilder;
import javafx.animation.TranslateTransitionBuilder;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


@SuppressWarnings("deprecation")
public class Main extends Application {

	final static int WIDTH = 1000;
	final static int HEIGHT = 600;

	final static Image BACKGROUND_IMAGE = new Image(Main.class.getResource("images/background.png").toString());

	final static Image TREE_1_IMAGE = new Image(Main.class.getResource("images/kaktus1.png").toString());
	final static Image TREE_2_IMAGE = new Image(Main.class.getResource("images/kaktus2.png").toString());
	final static Image TREE_3_IMAGE = new Image(Main.class.getResource("images/kaktus3.png").toString());
	final static Image ROCK_IMAGE = new Image(Main.class.getResource("images/rock1.png").toString());

	final static Image BIRD_1_IMAGE = new Image(Main.class.getResource("images/bird1.png").toString());
	final static Image BIRD_2_IMAGE = new Image(Main.class.getResource("images/bird2.png").toString());

	private final static Random RANDOM = new Random();

	private Group bird;
	private Animation current;
	public IntegerProperty hitCounter = new SimpleIntegerProperty(this, "hitCounter");
	private IntegerProperty shotCounter = new SimpleIntegerProperty(this, "shotCounter");

	private final int REMAINED_SHOTS = 20;

	@FXML
    private GameOverController gameOverController;

	public static Main main;
	public Main() {
		main=this;
	}

	@Override
	public void start(Stage primaryStage) {
		final ImageView tree1 = new ImageView(TREE_1_IMAGE);
		tree1.setTranslateX(150);
		tree1.setTranslateY(80);
		final ImageView tree2 = new ImageView(TREE_2_IMAGE);
		tree2.setTranslateX(380);
		tree2.setTranslateY(18);
		final ImageView tree3 = new ImageView(TREE_3_IMAGE);
		tree3.setTranslateX(730);
		tree3.setTranslateY(60);
		final ImageView rock = new ImageView(ROCK_IMAGE);
		rock.setTranslateX(762);
		rock.setTranslateY(302);
		final Group foreground = new Group(tree1, tree2, tree3, rock);
		foreground.setEffect(new DropShadow());

		final ImageView bird1 = new ImageView(BIRD_1_IMAGE);
		final ImageView bird2 = new ImageView(BIRD_2_IMAGE);
		bird = new Group(bird1, bird2);

		final ImageView background = new ImageView(BACKGROUND_IMAGE);
		background.setEffect(new BoxBlur());
		background.setOpacity(0.9);

		TimelineBuilder.create()
			.cycleCount(Animation.INDEFINITE)
			.keyFrames(
					new KeyFrame(Duration.millis(150), new EventHandler<ActionEvent>(){
						@Override
						public void handle(ActionEvent actionEvent) {
							bird.getChildren().setAll(bird2);
						}
					}),
					new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>(){
						@Override
						public void handle(ActionEvent actionEvent) {
							bird.getChildren().setAll(bird1);
						}
					})
					)
			.build().play();

		final Animation hitAnimation = SequentialTransitionBuilder.create()
				.node(bird)
				.children(RotateTransitionBuilder.create()
						.fromAngle(0)
						.toAngle(1260)
						.duration(Duration.millis(800))
						.build(),
					TranslateTransitionBuilder.create()
						.byY(1000)
						.duration(Duration.millis(800))
						.build()
				)
				.onFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent arg0) {
						if(shotCounter.get() > 0)
							startAnimation();
						else {
							try {
								newView(hitCounter.get());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				})
				.build();


		bird.setOnMousePressed(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				current.stop();
				hitAnimation.play();
				hitCounter.set(hitCounter.get()+25);
			}
		});

			final Text hitLabel = new Text();
			hitLabel.textProperty().bind(Bindings.concat("Punkty: ", hitCounter));
			final Text shotLabel = new Text();
			shotLabel.textProperty().bind(Bindings.concat("Pozostalo strzalow: ", shotCounter));

			final VBox hud = VBoxBuilder.create().children(hitLabel, shotLabel)
					.translateX(20)
					.translateY(20)
					.build();

			final Group root = new Group(background, bird, foreground, hud);
			shotCounter.set(REMAINED_SHOTS);

			try {
				Scene scene = new Scene(root,WIDTH,HEIGHT);
				scene.setOnMouseClicked(new EventHandler<MouseEvent>(){
					@Override
					public void handle(MouseEvent event) {
						String gunSound = null;
						shotCounter.set(shotCounter.get()-1);
						if(shotCounter.get() < 0) {
							shotCounter.set(0);
							gunSound = new File("res/sound/gunempty.mp3").toURI().toString();
						} else {
							gunSound = new File("res/sound/gunshot.mp3").toURI().toString();

						}
						MediaPlayer gunPlayer = new MediaPlayer(new Media(gunSound));
						gunPlayer.play();
					}

				});
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.setCursor(Cursor.CROSSHAIR);
			String opening = new File("res/sound/goodbadugly.mp3").toURI().toString();
			MediaPlayer player = new MediaPlayer(new Media(opening));
			player.play();
			primaryStage.setScene(scene);
			primaryStage.show();

			startAnimation();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}


	private void startAnimation() {
		if(current != null) {
			current.stop();
		}
		final int y0 = RANDOM.nextInt(HEIGHT/2) + HEIGHT/4;
		final int y1 = RANDOM.nextInt(HEIGHT/2) + HEIGHT/4;

		bird.setRotate(0);
		current = TranslateTransitionBuilder.create()
					.node(bird)
					.fromX(-100)
					.toX(WIDTH)
					.fromY(y0)
					.toY(y1)
					.duration(Duration.seconds(2))
					.onFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							if(shotCounter.get() > 0)
								startAnimation();
							else {
								try {
									newView(hitCounter.get());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}).build();
		current.play();
	}

	public void newView(int score) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/gameOverView.fxml"));
		Parent root = (Parent)fxmlLoader.load();

		gameOverController = fxmlLoader.<GameOverController>getController();
		gameOverController.setScoreLabel("Twój wynik: "+score);
		Stage stage = new Stage();
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("Game Over");
		stage.show();

	}

	public void restartApplication() {
		hitCounter.set(0);
		shotCounter.set(REMAINED_SHOTS);
		String opening = new File("res/sound/goodbadugly.mp3").toURI().toString();
		MediaPlayer player = new MediaPlayer(new Media(opening));
		player.play();
		startAnimation();
	}

}
