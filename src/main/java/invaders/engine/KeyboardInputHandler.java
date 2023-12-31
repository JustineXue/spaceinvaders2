package invaders.engine;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyboardInputHandler {
    private final GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private boolean removeSlow = false;
    private boolean removeFast = false;
    private boolean undo = false;
    private boolean switchEasy = false;
    private boolean switchMedium = false;
    private boolean switchHard = false;

    private Map<String, MediaPlayer> sounds = new HashMap<>();

    KeyboardInputHandler(GameEngine model) {
        this.model = model;

        // TODO (longGoneUser): Is there a better place for this code?
        URL mediaUrl = getClass().getResource("/shoot.wav");
        String jumpURL = mediaUrl.toExternalForm();

        Media sound = new Media(jumpURL);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.put("shoot", mediaPlayer);
    }

    void handlePressed(KeyEvent keyEvent) {
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.SPACE)) {
            if (model.shootPressed()) {
                MediaPlayer shoot = sounds.get("shoot");
                shoot.stop();
                shoot.play();
            }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }
        if (keyEvent.getCode().equals(KeyCode.S)){
            removeSlow = true;
        }
        if (keyEvent.getCode().equals(KeyCode.F)){
            removeFast = true;
        }
        if (keyEvent.getCode().equals(KeyCode.U)){
            undo = true;
        }
        if (keyEvent.getCode().equals(KeyCode.E)){
            switchEasy = true;
        }
        if (keyEvent.getCode().equals(KeyCode.M)){
            switchMedium = true;
        }
        if (keyEvent.getCode().equals(KeyCode.H)){
            switchHard = true;
        }

        if (left) {
            model.leftPressed();
        }

        if(right){
            model.rightPressed();
        }

        if(removeSlow){
            this.model.removeSlowProjectiles();
        }
        if(removeFast){
            this.model.removeFastProjectiles();
        }
        if(undo){
            this.model.restoreMemento();
        }
        if(switchEasy){
            this.model.switchEasy();
        }
        if(switchMedium){
            this.model.switchMedium();
        }
        if(switchHard){
            this.model.switchHard();
        }
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
            model.leftReleased();
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            model.rightReleased();
            right = false;
        }
        if (keyEvent.getCode().equals(KeyCode.S)){
            removeSlow = false;
        }
        if (keyEvent.getCode().equals(KeyCode.F)){
            removeFast = false;
        }
        if (keyEvent.getCode().equals(KeyCode.U)){
            undo = false;
        }
        if (keyEvent.getCode().equals(KeyCode.E)){
            switchEasy = false;
        }
        if (keyEvent.getCode().equals(KeyCode.M)){
            switchMedium = false;
        }
        if (keyEvent.getCode().equals(KeyCode.H)){
            switchHard = false;
        }
    }
}
