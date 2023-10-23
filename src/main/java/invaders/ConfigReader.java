package invaders;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;

public class ConfigReader {
    private JSONObject gameInfo;
    private JSONObject playerInfo;
    private JSONArray bunkersInfo;
    private JSONArray enemiesInfo;

    public ConfigReader(){}

    public void parse(String configPath){
        JSONParser parser = new JSONParser();
        try {
            JSONObject configObject = (JSONObject) parser.parse(new FileReader(configPath));

            // Reading game section
            this.gameInfo = (JSONObject) configObject.get("Game");

	        // Reading player section
            this.playerInfo = (JSONObject) configObject.get("Player");

			// Reading bunker section
            this.bunkersInfo = (JSONArray) configObject.get("Bunkers");

            // Reading enemies section
            this.enemiesInfo = (JSONArray) configObject.get("Enemies");
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

    public JSONObject getGameInfo() {
        return this.gameInfo;
    }

    public JSONObject getPlayerInfo() {
        return this.playerInfo;
    }

    public JSONArray getBunkersInfo() {
        return this.bunkersInfo;
    }

    public JSONArray getEnemiesInfo() {
        return this.enemiesInfo;
    }
}