package com.example.javafxmysqltemplate;

import com.example.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class ProjectController {

    @FXML
    protected void goToDiscordIdInsert(ActionEvent event)
    {
        navigateTo("discordIdInsert.fxml", event);
    }

    @FXML
    private void goToRiotAccountInfo(ActionEvent event) {
        navigateTo("RiotAccountInfo.fxml", event);
    }

    @FXML
    public void gotToRolePage(ActionEvent event) {
        navigateTo("rolePage.fxml", event);
    }

    public void goToDeleteUser(ActionEvent event) {
        navigateTo("deleteUser.fxml", event);
    }

    @FXML
    public Button submit;

    @FXML
    public void goToCreateTeams(ActionEvent event) {
        navigateTo("createTeams.fxml", event);
    }

    @FXML
    private void goToHomePage(ActionEvent event)
    {
        navigateTo("welcomePage.fxml", event);
    }

    private void navigateTo(String fxmlFile, ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get the stage from the current event source (button)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @FXML
    private TextField discordIdTextField;
    @FXML
    private TextField RiotIdTextField;

    @FXML
    protected void addPlayerInfo()
    {
        String text = discordIdTextField.getText();

        try (Connection connection = Database.newConnection()) {
            String sql = "INSERT INTO player (DiscordId) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, text);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                output.setText("This player was added");
            }

            discordIdTextField.clear();
        } catch (SQLException e) {
            output.setText("This player already exists");
        }
    }

    @FXML
    private ComboBox<String> userComboBox = new ComboBox<>();

    @FXML
    protected void accountInfo()
    {
        String riotId = RiotIdTextField.getText();
        String[] substring = riotId.split("#");

        try (Connection connection = Database.newConnection()) {
            String selectedUser = userComboBox.getValue();

            String toInsert = "INSERT INTO account (RiotUsername, RiotTagline, DiscordId, link, accountRank) VALUES (?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(toInsert);
            statement.setString(1, substring[0]);      // Set RiotUsername
            statement.setString(2, substring[1]);      // Set RiotTagline
            statement.setString(3, selectedUser);      // Set DiscordId
            statement.setString(4, "https://www.op.gg/summoners/na/" + substring[0] + "-" + substring[1]);
            statement.setString(5, getRankNumber(substring[0], substring[1]) + "");

            statement.executeUpdate();
            output.setText("Added this account to " + selectedUser);

            RiotIdTextField.clear();
        } catch (SQLException e) {
            if(userComboBox.getValue().equals("Select a User"))
                output.setText("Select a user");
            else
                output.setText("Account added");
        }

    }

    public void deleteUser()
    {
        try(Connection connection = Database.newConnection())
        {
            connection.prepareStatement("delete from account where discordId = '" + userComboBox.getValue() + "'").execute();
            connection.prepareStatement("delete from plays where discordId = '" + userComboBox.getValue() + "'").execute();
            connection.prepareStatement("delete from player where discordId = '" + userComboBox.getValue() + "'").execute();

            output.setText("The player " + userComboBox.getValue() + "\nwas deleted");
        } catch(Exception e)
        {
            e.printStackTrace(System.err);
        }
    }

    @FXML
    public CheckBox isToplaner = new CheckBox();
    @FXML
    public CheckBox isJungler = new CheckBox();
    @FXML
    public CheckBox isMidlaner = new CheckBox();
    @FXML
    public CheckBox isBotlaner = new CheckBox();
    @FXML
    public CheckBox isSupport = new CheckBox();

    @FXML
    public void inputRoles()
    {
        try (Connection connection = Database.newConnection()) {
                String toPlays = "INSERT INTO plays (playsId, DiscordId, RoleName, EloOffset) " +
                        "Values (?, ?, ?, 0)";
                //eloOffset is 0 until updated later

            PreparedStatement playsStatement = connection.prepareStatement(toPlays);
            playsStatement.setString(2, userComboBox.getValue());      // Set DiscordId


            addRole(playsStatement, isToplaner.isSelected(), "Toplane", connection);
            addRole(playsStatement, isJungler.isSelected(), "Jungle", connection);
            addRole(playsStatement, isMidlaner.isSelected(), "Midlane", connection);
            addRole(playsStatement, isBotlaner.isSelected(), "Botlane", connection);
            addRole(playsStatement, isSupport.isSelected(), "Support", connection);

            output.setText(userComboBox.getValue() + " roles where updated successfully");
        }
        catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    @FXML
    private void addRole(PreparedStatement playsStatement, boolean isLane, String laneName, Connection connection)
    {
        try {
            PreparedStatement hasRole = connection.prepareStatement("select * from plays where " +
                    "DiscordId = (?) AND RoleName = (?)");
            hasRole.setString(1, userComboBox.getValue());
            hasRole.setString(2, laneName);
            ResultSet roleEx = hasRole.executeQuery();
            boolean roleInDatabase = roleEx.next();
            if (isLane && !roleInDatabase)
            {
                int playsId = getNextId("Plays");

                playsStatement.setInt(1, playsId);
                //set playsId
                playsStatement.setString(3, laneName);
                //set laneName
                playsStatement.execute();
            } else if(!isLane && roleInDatabase) {
                PreparedStatement deleteRole = connection.prepareStatement("delete from plays where DiscordId = (?) and " +
                        "RoleName = (?)");
                deleteRole.setString(1, userComboBox.getValue());
                deleteRole.setString(2, laneName);
                deleteRole.execute();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @FXML
    public ComboBox<String> player1 = new ComboBox<>();
    @FXML
    public ComboBox<String> player2 = new ComboBox<>();
    @FXML
    public ComboBox<String> player3 = new ComboBox<>();
    @FXML
    public ComboBox<String> player4 = new ComboBox<>();
    @FXML
    public ComboBox<String> player5 = new ComboBox<>();
    @FXML
    public ComboBox<String> player6 = new ComboBox<>();
    @FXML
    public ComboBox<String> player7 = new ComboBox<>();
    @FXML
    public ComboBox<String> player8 = new ComboBox<>();
    @FXML
    public ComboBox<String> player9 = new ComboBox<>();
    @FXML
    private ComboBox<String> player10 = new ComboBox<>();

    public void initialize()
    {
        loadUsersFromDatabase(userComboBox);
        loadUsersFromDatabase(player1);
        loadUsersFromDatabase(player2);
        loadUsersFromDatabase(player3);
        loadUsersFromDatabase(player4);
        loadUsersFromDatabase(player5);
        loadUsersFromDatabase(player6);
        loadUsersFromDatabase(player7);
        loadUsersFromDatabase(player8);
        loadUsersFromDatabase(player9);
        loadUsersFromDatabase(player10);
    }

    private void loadUsersFromDatabase(ComboBox<String> comboBox)
    {
        List<String> users = new ArrayList<>();

        try (Connection connection = Database.newConnection()) {
            String sql = "SELECT DiscordId from player";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String username = resultSet.getString("DiscordId");
                users.add(username);
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }

        comboBox.getItems().addAll(users);
    }

    int teamIndex;

    public void createTeams()
    {
        teamIndex = 0;
        try (Connection connection = Database.newConnection()) {
            ArrayList<Player> players = new ArrayList<>();
            String getData =
                    "SELECT DISTINCT player.DiscordId, " +
                            "MAX(CASE WHEN player.eloOverride > account.accountRank THEN player.eloOverride " +
                            "ELSE account.accountRank END" +
                            ") AS maxEffectiveRank, plays.RoleName " +
                            "FROM player " +
                            "JOIN plays ON player.DiscordId = plays.DiscordId " +
                            "JOIN account ON account.DiscordId = plays.DiscordId " +
                            "WHERE player.DiscordId = (?) " +
                            "GROUP BY player.DiscordId, plays.RoleName;";

            PreparedStatement playsStatement = connection.prepareStatement(getData);

            addPlayerToList(players, playsStatement, player1);
            addPlayerToList(players, playsStatement, player2);
            addPlayerToList(players, playsStatement, player3);
            addPlayerToList(players, playsStatement, player4);
            addPlayerToList(players, playsStatement, player5);
            addPlayerToList(players, playsStatement, player6);
            addPlayerToList(players, playsStatement, player7);
            addPlayerToList(players, playsStatement, player8);
            addPlayerToList(players, playsStatement, player9);
            addPlayerToList(players, playsStatement, player10);

            ArrayList<Team> teams = TeamMatchupBuilder.createAllTeams(players.toArray(new Player[0]));

            TeamMatchupBuilder.sortTeams(teams);

            match = new ArrayList<>();
            for(int i = 0; i < teams.size() - 1; ++i)
            {
                for(int j = i + 1; j < teams.size(); ++j)
                {
                    TeamMatchupBuilder.validMatch(match, teams.get(i), teams.get(j));
                }
            }

            if(match.isEmpty())
            {
                output.setText("Something is wrong with one of hte players D:");

                return;
            }

            TeamMatchupBuilder.sortMatchup(match);
            setOutputText(teamIndex);

        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
    }

    public ArrayList<Matchup> match;

    @FXML
    public TextArea output;

    private void setOutputText(int teamIndex) {
        output.setText(match.get(teamIndex).toString());
    }

    public void addPlayerToList(ArrayList<Player> players, PreparedStatement playsStatement, ComboBox<String> player)
    {
        try {
            playsStatement.setString(1, player.getValue());
            ResultSet resultSet = playsStatement.executeQuery();

            resultSet.next();

            String playerName = resultSet.getString("DiscordId");
            double playerElo = resultSet.getDouble("maxEffectiveRank");
            ArrayList<String> roles = new ArrayList<>();

            do {
                roles.add(resultSet.getString("RoleName"));
            } while (resultSet.next());

            players.add(new Player(playerName, playerElo, roles.toArray(new String[0])));

        } catch (SQLException e) {
            System.out.println(player);
            e.printStackTrace(System.err);
        }
    }

    @FXML
    public Button next;

    public void updateTeam()
    {
        teamIndex = (teamIndex + 1) % match.size();
        setOutputText(teamIndex);
    }

    @FXML
    public Button ConformTeam;

    @FXML
    public CheckBox BlueWon;

    public void addMatch()
    {
        Matchup match = this.match.get(teamIndex);
        try(Connection connection = Database.newConnection())
        {
              String addTeams = "insert into team " +
                    "(TeamId, TeamAvgElo, Player1, Player2, Player3, Player4, Player5) " +
                    "Values (?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement teamStatement = connection.prepareStatement(addTeams);

            int blueId = addTeams(match.blueTeam, teamStatement);
            int redId = addTeams(match.redTeam, teamStatement);

            match.updateNumbers();

            String addMatch = "insert into matchup " +
                    "(MatchupId, BlueTeam, RedTeam, EloDist, BlueWon, Date) values " +
                    "(?, ?, ?, ?, ?, ?)";

            PreparedStatement matchStatement = connection.prepareStatement(addMatch);

            int matchId = getNextId("matchup");
            matchStatement.setInt(1, matchId);
            matchStatement.setInt(2, blueId);
            matchStatement.setInt(3, redId);
            matchStatement.setDouble(4,match.getTeamEloDiff());
            matchStatement.setBoolean(5, BlueWon.isSelected());
            matchStatement.setDate(6, Date.valueOf(LocalDate.now()));

            matchStatement.execute();

            updatePlayersOffset(match, BlueWon.isSelected());

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void updatePlayersOffset(Matchup match, boolean blueWon)
    {
        try(Connection connection = Database.newConnection())
        {
            PreparedStatement statement = connection.prepareStatement(
                    "update plays " +
                            "set EloOffset = EloOffset + (?) " +
                            "where plays.DiscordId = (?) " +
                            "and plays.RoleName = (?)");
            for (int i = 0; i < 5; i++) {
                updateEloOffset(statement, match.blueTeam.getPlayers()[i], match.getTeamEloDiff(), blueWon, i);
                updateEloOffset(statement, match.redTeam.getPlayers()[i], match.getTeamEloDiff(), !blueWon, i);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void updateEloOffset(PreparedStatement statement, Player player, double eloDiff, boolean increase, int role) {
        try
        {
            String roleName = switch(role) {
                case 1 -> "Jungle";
                case 2 -> "Midlane";
                case 3 -> "Botlane";
                case 4 -> "Support";
                default -> "Toplane";
            };
            statement.setString(2, player.name);
            statement.setString(3, roleName);
            if(!increase) {
                eloDiff *= -1;
            }

            statement.setDouble(1, eloDiff);

            statement.execute();
        }catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private int addTeams(Team team, PreparedStatement statement)
    {
        int id = (getNextId("team"));
        try(Connection connection = Database.newConnection()) {
            PreparedStatement statement1 = connection.prepareStatement("select TeamId from team where " +
                    "player1 = (?) and " +
                    "player2 = (?) and " +
                    "player3 = (?) and " +
                    "player4 = (?) and " +
                    "player5 = (?) ");

            statement1.setString(1, team.players[0].getName());
            statement1.setString(2, team.players[1].getName());
            statement1.setString(3, team.players[2].getName());
            statement1.setString(4, team.players[3].getName());
            statement1.setString(5, team.players[4].getName());

            //if here is no team it will fail and go to the catch block
            try(ResultSet resultSet = statement1.executeQuery())
            {
                resultSet.next();
                int teamId = resultSet.getInt("TeamId");

                PreparedStatement updatePlayersElo = connection.prepareStatement(
                        "update account " +
                        "set accountRank = (?) " +
                        "where discordId = (?) " +
                        "and RiotUsername = (?) " +
                        "and RiotTagline = (?) ");

                PreparedStatement getAccounts = connection.prepareStatement(
                        "select RiotUserName, RiotTagline from account where discordId = (?)"
                );

                updateAccountsInfo(team.getPlayers()[0], getPlayersAccount(getAccounts, team.getPlayers()[0]), updatePlayersElo);
                updateAccountsInfo(team.getPlayers()[1], getPlayersAccount(getAccounts, team.getPlayers()[1]), updatePlayersElo);
                updateAccountsInfo(team.getPlayers()[2], getPlayersAccount(getAccounts, team.getPlayers()[2]), updatePlayersElo);
                updateAccountsInfo(team.getPlayers()[3], getPlayersAccount(getAccounts, team.getPlayers()[3]), updatePlayersElo);
                updateAccountsInfo(team.getPlayers()[4], getPlayersAccount(getAccounts, team.getPlayers()[4]), updatePlayersElo);

                team.updateAveElo();

                PreparedStatement update = connection.prepareStatement("Update team " +
                        "set team.TeamAvgElo = " + team.aveElo +
                        "where TeamId = " + teamId);
                update.execute();
                return teamId;
            } catch(Exception e) {
                statement.setInt(1, id);
                statement.setDouble(2, team.aveElo);
                statement.setString(3, team.players[0].getName());
                statement.setString(4, team.players[1].getName());
                statement.setString(5, team.players[2].getName());
                statement.setString(6, team.players[3].getName());
                statement.setString(7, team.players[4].getName());

                statement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return id;
    }

    private void updateAccountsInfo(Player player, String[][] playersAccount, PreparedStatement updatePlayersElo) {
        try {
            updatePlayersElo.setString(2, player.getName());
            for(String[] account : playersAccount)
            {
                updateAccountInfo(account, updatePlayersElo);
            }
        }  catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void updateAccountInfo(String[] account, PreparedStatement updatePlayersElo)
    {
        try {
            updatePlayersElo.setDouble(1, getRankNumber(account[0], account[1]));
            updatePlayersElo.setString(3, account[0]);
            updatePlayersElo.setString(4, account[1]);
            updatePlayersElo.execute();
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private String[][] getPlayersAccount(PreparedStatement getAccounts, Player player)
    {
        try {
            getAccounts.setString(1, player.name);
            try(ResultSet resultSet = getAccounts.executeQuery())
            {
                ArrayList<String[]> accounts = new ArrayList<>();
                while(resultSet.next())
                {
                    String[] account = new String[2];
                    account[0] = resultSet.getString("RiotUserName");
                    account[1] = resultSet.getString("RiotTagline");
                    accounts.add(account);
                }
                return accounts.toArray(new String[0][0]);
            } catch(Exception e) {
                e.printStackTrace(System.err);
            }
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }

        return new String[0][0];
    }

    private int getNextId(String table)
    {
        try(Connection connection = Database.newConnection())
        {
            ResultSet resultSet = connection.prepareStatement(
                    "SELECT COALESCE(" +
                            "(SELECT MIN(" + table + "Id) + 1 " +
                            "AS nextId " +
                            "FROM " + table +
                            " WHERE (" + table + "Id + 1) " +
                             "NOT IN (SELECT " + table + "Id FROM " + table + ")), 1 ) " +
                            "AS nextId;").executeQuery();
            resultSet.next();
            return resultSet.getInt("nextId");
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }

        return 1;
    }

    private double getRankNumber(String riotUsername, String riotTagline)
    {
        String [] data1 = RiotApiController.getPlayerRankedInfo(riotUsername, riotTagline)[0];
        if(data1[0].equals("No Ranked Data"))
            return 0;
        else if(data1[0].equals("Summoner Not Found")) {
            output.setText("Summoner Not Found");
            return 0;
        }
        else {
            String[] data2 = RiotApiController.getPlayerRankedInfo(riotUsername, riotTagline)[1];
            return Math.max(RiotApiController.eloCalc(data1), RiotApiController.eloCalc(data2));
        }
    }
}