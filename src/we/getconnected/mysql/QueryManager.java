package we.getconnected.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import model.Answer;
import model.Continent;
import model.Country;
import model.Question;
import model.User;

public class QueryManager {

    private final Dbmanager dbmanager;

    public QueryManager(Dbmanager dbmanager) {
        this.dbmanager = dbmanager;
    }

     public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<User>();
        try {
            String sql = "SELECT * FROM user";
            ResultSet result = dbmanager.doQuery(sql);
            while(result.next()) {
                users.add(new User(
                        result.getInt("user_id"),
                        result.getString("firstname"),
                        result.getString("lastname"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getBoolean("teacher"),
                        getUsergroup(result.getInt("user_id"))
                        ));
            }
        } catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return users;
    }
    
    public User getUser(String username){
        User user=  null; 
        try {
            String sql = "SELECT * FROM user WHERE username='" + username + "'";
            ResultSet result = dbmanager.doQuery(sql);
            while(result.next()) {
                user = new User(
                        result.getInt("user_id"),
                        result.getString("firstname"),
                        result.getString("lastname"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getBoolean("teacher"),
                        getUsergroup(result.getInt("user_id"))
                        );
            }
        } catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return user;
    }
    
    public Continent getContinent(String name, int user_id){
        Continent continent =  null; 
        try {
            String sql = "SELECT * FROM continent WHERE name='" + name + "'";
            ResultSet result = dbmanager.doQuery(sql);
            while(result.next()) {
                continent = new Continent(result.getInt("continent_id"),
                        name, user_id);
            }
        } catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return continent;
    }
    
    public String getUsergroup(int userID){
        String groupName = "";
         try {
            String sql = "SELECT groupName FROM groep INNER JOIN user_group ON groep.group_id=user_group.group_id WHERE user_group.user_id = "+userID;
            ResultSet result = dbmanager.doQuery(sql);
            if(result.next()) {
                groupName = result.getString("groupName");
            }
        } catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return groupName;
    }
    
    public ArrayList<Country> getUserCountries(int user_id){
        ArrayList<Country> landen = new ArrayList<Country>();
        
        try {
            String sql = "SELECT country.country_id, country.name, user_country.completed " + 
                    "FROM country " +
                    "INNER JOIN user_country " + 
                    "ON country.country_id=user_country.country_id " +
                    "WHERE user_country.user_id = " + user_id;
            ResultSet result = dbmanager.doQuery(sql);
            while(result.next()) {
                landen.add(new Country(result.getInt("country_id"),
                        Country.Countries.fromString(result.getString("name")),
                        getUserQuestions(user_id, result.getInt("country_id")),
                        result.getBoolean("completed")));
            }
        } catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return landen;
    }
    
    public ArrayList<Question> getUserQuestions(int user_id, int country_id){
        ArrayList<Question> questions = new ArrayList<Question>();
        try {
            ;
            String sql = "SELECT question.question_id, question.question, question.map,user_question.complete,user_question.tries,user_question.available " +
                    "FROM question " +
                    "INNER JOIN user_question ON question.question_id=user_question.question_id " +
                    "WHERE user_question.user_id = " + user_id + " " +
                    "AND question.country_id = " + country_id;
            ResultSet result = dbmanager.doQuery(sql);
            
            while(result.next()) {
                questions.add(new Question(result.getInt("question_id"),
                        result.getString("question"),
                        result.getString("map"),
                        getAnswers(result.getInt("question_id")),
                        result.getByte("complete"),
                        result.getInt("tries"),
                        result.getTimestamp("available")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return questions;
    }
    
    public ArrayList<Answer> getAnswers(int question_id){
        ArrayList<Answer> answers = new ArrayList<Answer>();
        try {
            String sql = "SELECT * FROM answer WHERE question_id = " + question_id;
            ResultSet result = dbmanager.doQuery(sql);
            while(result.next()) {
                answers.add(new Answer(result.getInt("x"),
                        result.getInt("y"),
                        result.getByte("correct"),
                        result.getString("answer")));
            }
        } catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return answers;
    }
    
    public Timestamp getDate(){
        Timestamp date = null;
        try{
            String query = "SELECT CURRENT_TIMESTAMP();";
            ResultSet result = dbmanager.doQuery(query);
            if (result.next()){
                date = result.getTimestamp("CURRENT_TIMESTAMP()");
            }
        }
        catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return date;
    }
    
    public int getLastQuestionId(){
        int question_id = 0;
        try{
            String query = "SELECT question_id FROM question ORDER BY question_id DESC LIMIT 1";
            ResultSet result = dbmanager.doQuery(query);
            if (result.next()){
                question_id = result.getInt("question_id");
            }
        }
        catch (SQLException e) {
            System.out.println(Dbmanager.SQL_EXCEPTION + e.getMessage());
        }
        return question_id;
    }
    
    public void insertQuestion(Question question){
        String query = "INSERT INTO question (question_id, question, map, country_id) VALUES ("
                + "" + question.getQuestion_Id() + ", "
                + "'" + question.getQuestion() + "', "
                + "'" + question.getMap().toString() + "', "
                + "" + question.getCountry_id() + ")";
        }
        public void insertAnswers(ArrayList<Answer> answers){
        
        }
    
    public void updateUserQuestion(Question question, int user_id){
        String query = "UPDATE user_question SET tries= " + question.getTries() + ", complete=" + (question.isCorrect()?1:0) + 
                ", available = '" + question.getAvailable() + "'" +
                " WHERE user_id = " + user_id + 
                " AND question_id = " + question.getQuestion_Id();
        dbmanager.insertQuery(query);
    }
    
    public void updateUserCountry(Country country, int user_id){
        String query = "UPDATE user_country SET completed = " + (country.isCompleted()?1:0) + 
                " WHERE user_id = " + user_id +
                " AND country_id = " + country.getCountry_id();
        dbmanager.insertQuery(query);
    }
}