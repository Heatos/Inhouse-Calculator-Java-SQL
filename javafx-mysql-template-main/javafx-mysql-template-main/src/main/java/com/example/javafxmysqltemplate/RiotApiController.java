package com.example.javafxmysqltemplate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RiotApiController {
    private static final String API_KEY;

    public static double eloCalc(String[] data)
    {
        if(data[0] == null)
        {
            return 0;
        }

        double out = 0;
        switch (data[0]){
            case "CHALLENGER", "GRANDMASTERS", "MASTER": out += 400;
            case "DIAMOND": out+= 400;
            case "EMERALD": out += 400;
            case "PLATINUM": out += 400;
            case "GOLD": out += 400;
            case "SILVER": out += 400;
            case "BRONZE": out += 400;
            default: out+= 0;
        }

        switch(data[1])
        {
            case "I": out += 100;
            case "II": out += 100;
            case "III": out += 100;
            default: out += 0;
        }

        out += Integer.parseInt(data[2]);

        double top = Integer.parseInt(data[3]);
        double bottom = Integer.parseInt(data[4]);

        //win rate bonus

        if(out > 0)
        {
            out += (int) (((top / (top + bottom)) - .5) * 1000);
        }

        return out;
    }

    public static String[][] getPlayerRankedInfo(String gameName, String tagLine) {
        try {
            // Fetch PUUID from Riot ID
            String riotUrl = "https://americas.api.riotgames.com/riot/account/v1/accounts/by-riot-id/"
                    + encodeURIComponent(gameName) + "/" + encodeURIComponent(tagLine) + "?api_key=" + API_KEY;
            org.json.JSONObject riotData = getData(riotUrl);

            if (riotData.has("puuid")) {
                String puuid = riotData.getString("puuid");

                // Fetch encrypted ID using PUUID
                String summonerUrl = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/"
                        + encodeURIComponent(puuid) + "?api_key=" + API_KEY;
                org.json.JSONObject summonerData = getData(summonerUrl);

                if (summonerData.has("id")) {
                    String encryptedId = summonerData.getString("id");

                    // Fetch ranked data using encrypted ID
                    String leagueUrl = "https://na1.api.riotgames.com/lol/league/v4/entries/by-summoner/"
                            + encodeURIComponent(encryptedId) + "?api_key=" + API_KEY;
                    org.json.JSONArray leagueData = getDataArray(leagueUrl);

                    if (!leagueData.isEmpty()) {

                        String [][] out = new String[2][5];
                        for(int i = 0; i < leagueData.length(); ++i)
                        {
                            String[] current = new String[5];
                            org.json.JSONObject entry = leagueData.getJSONObject(i);
                            current[0] = entry.getString("tier");
                            current[1] = entry.getString("rank");
                            current[2] = String.valueOf(entry.getInt("leaguePoints"));
                            current[3] = String.valueOf(entry.getInt("wins"));
                            current[4] = String.valueOf(entry.getInt("losses"));
                            out[i] = current;
                        }
                        return out;
                    } else {
                        return new String[][]{new String[]{"No Ranked Data"}};
                    }
                }
            }
        } catch (Exception e) {
            return new String[][]{new String[]{"Summoner Not Found"}};
        }
        return new String[][]{new String[]{"Summoner Not Found"}};
    }

    private static org.json.JSONObject getData(String urlString) throws Exception {
        String response = getRequest(urlString);
        return new org.json.JSONObject(response);
    }

    private static org.json.JSONArray getDataArray(String urlString) throws Exception {
        String response = getRequest(urlString);
        return new org.json.JSONArray(response);
    }

    private static String getRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Riot-Token", API_KEY);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return content.toString();
    }

    private static String encodeURIComponent(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }

}
