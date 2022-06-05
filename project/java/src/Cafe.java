/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Cafe

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Cafe.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Cafe esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Menu");
                System.out.println("2. Update Profile");
                System.out.println("3. Place a Order");
                System.out.println("4. Update a Order");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: Menu(esql); break;
                   case 2: UpdateProfile(esql, authoriedUser); break;
                   case 3: PlaceOrder(esql, authoriedUser); break;
                   case 4: UpdateOrder(esql); break;
                   case 9: usermenu = false; break;
                   //===================================
                   case 
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
	    String type="Customer";
	    String favItems="";

				 String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

/*I am not to sure is I need to put "sting authorisedUser" in the void function too  */
  public static void Menu(Cafe esql){
      System.out.println("Cafe Menu");
      /*Trying to display all item names and prices from menu */
      String query = String.format("SELECT itemName, price FROM MENU"); 
      esql.executeQueryAndPrintResult(query);
      System.out.println("=================");
      System.out.println("1. Search For Item");
      System.out.println("2. Edit Menu");
      switch(readChoice()){
         case 1: SearchItem(esql);break;  
         case 2: UpdateMenu(esql, authorisedUser);break; /*I am not to sure is I need to put "sting authorisedUser" in the void function too  */
         default : system.out.println("Unrecognized Choice!");break;
      }
     /* ask for login and do the select type from user having such login and check if it's manager */
     
  }
      
  /*Made a list of commands for the user to update his/her profile */    
  public static void UpdateProfile(Cafe esql, String authorisedUser){
      boolean profile = true;
      while(profile){
         System.out.println("---Updating Profile---");
         System.out.println("====================================");
         System.out.println("1. Change Password");
         System.out.println("2. Change Phone Number");
         System.out.println("3. Change Favorite Item");
         System.out.println("4. Change User Type Authority");
         System.out.println("====================================");
         System.out.println("9. Log out");
         switch (readChoice()){
            case 1: ChangePassword(esql, authorisedUser);break;
            case 2: ChangePhoneNumber(esql, authorisedUser);break;
            case 3: ChangeFavItem(esql, authorisedUser);break;
            case 4: ChangeType(esql, authorisedUser);break;
            case 9: profile = false; break;
            default : System.out.println("Unrecognized Choice!"); break;
         }
      }
  }
 /* first show the previous orders(5 most recent for customers) or orders from the past 24 hours for the managers and employees), then 
    place new order */
  public static void PlaceOrder(Cafe esql, String authorisedUser){  //this is basically adding your order
      try{
         //System.out.print("\t Are you a customer? If so, provide your login");
         //String check = in.readLine();
         String check = String.format("SELECT type FROM USER WHERE login ='%s' AND type = 'Customer'", authorisedUser);
         esql.executeQueryAndPrintResult(query); //checking if it prints out results
         if(check.isEmpty()){
            String query = String.format("SELECT * FROM Orders WHERE timeStampReceived > DATE_SUB(NOW(), INTERVAL 24 HOUR)");
            //or you can do this for String query = String.format("SELECT * FROM Orders WHERE timeStampReceived > (NOW() - INTERVAL 24 HOUR)");
            esql.executeQueryAndPrintResult(query);
         }
         else{
            String query = String.format("SELECT * FROM (SELECT TOP 5 * FROM Orders ORDER BY orderid) WHERE login = '%s' ORDER BY orderid ", authoriedUser);
            esql.executeQueryAndPrintResult(query);  
         }

         String query = String.format("---Placing an Order---");
         System.out.println("=============================")
         System.out.println("Do you want to Add a Order? (Yes/No)");
         String edit = in.readLine();
         if("Yes".equalsIgnoreCase(edit)){
            System.out.println("---Adding item---");
            System.out.println("How many items you want to order?");
            int orderitems = in.readLine();
            float total=0;
            for(int i=0;i<orderitems;i++){
               System.out.println("Enter itemName:");
               String item=in.readLine();
               float cur=String.format("SELECT price FROM Menu WHERE itemName='%s'",item);
               total+=cur;
            }
             Timestamp curtime=NOW();
             boolean isPaid= false;
            String query = String.format("INSERT INTO Orders (orderid,login,paid,timeStampRecieved,total) VALUES('%s','%s','%s','%s','%f')",It,authorisedUser,isPaid,curtime,total);
            esql.executeQueryAndPrintResult(query); //timeStampRecieved=NOW() to get the current timestamp
            //in sql to make sure that the order is not paid then make sure: paid='false'
      }
      
  }



  public static void UpdateOrder(Cafe esql){  

  }

  public static void ChangePassword(Cafe esql, String authorisedUser){
      try{
         System.out.println("---We Are Now Changing Password--- ");
         System.out.println("\tPlease Enter New Password: ");
         String newPass = in.readLine();

         String query = String.format("UPDATE Users SET password = '%s' WHERE login = '%s'", newPass, authorisedUser);
         esql.executeUpdate(query);
         System.out.println("Passord is now Changed!");
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
  }

  public static void ChangePhoneNumber(Cafe esql, String authorisedUser){
      try{
         System.out.println("---We Are Now Changing Phone Number--- ");
         System.out.println("\tPlease Enter New Phone Number: ");
         String newPhoneNum = in.readLine();

         String query = String.format("UPDATE Users SET phoneNum = '%s' WHERE login = '%s'", newPhoneNum, authorisedUser);
         esql.executeUpdate(query);
         System.out.println("Phone Number is now Changed!");
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
  }

  public static void ChangeFavItem(Cafe eqsl, String authorisedUser){
   try{
      System.out.println("---We Are Now Changing Favorite Item--- ");
      System.out.println("\tPlease Enter New Favorite Item: ");
      String newFavItem = in.readLine();

      String query = String.format("UPDATE Users SET favItems = '%s' WHERE login = '%s'", newFavItem, authorisedUser);
      esql.executeUpdate(query);
      System.out.println("Your Favorite Item is now Changed!");
   }
   catch(Exception e){
      System.err.println (e.getMessage ());
   }

  }

  /* Made sure that the authorisedUser is a manager then asked customer login in order to be able 
     to change the customer to employe or manager */
  public static void ChangeType(Cafe esql, String authorisedUser){
   String query = String.format("SELECT type FROM USER WHERE login ='%s' AND type = 'Manager", authorisedUser);
   if(query.isEmpty()){
      System.out.println("You are not a manager So you are not able to change the types of autorization.");
   }
   else{
      try{  
         System.out.println("---Your Are a Manager! You're able to set other User from Customer to Manger or to Employee. --- ");
         System.out.println("====================================");
         System.out.println("1. Change Customer to Employee");
         System.out.println("2. Change Customer to Manager");
         System.out.println("====================================");
         System.out.println("\tEither enter \"1\" or \"2\"");
         String choice = in.readLine();
         //String option1 = "1";  we can use option1 for the if statement like--> if(option1.equals(choice)){} just in case the other dont works
         if("1".equals(choice)){
            System.out.println("Enter the User login of the Customer that you want to update there type:")
            String login = in.readLine();

            String query = String.format("UPDATE Users SET type = 'Employee' WHERE login = '%s'", login);
            esql.executeUpdate(query);
            System.out.println("Your Authority Type is now Changed!");
         }
         //String option2 = "2";  we can use option2 for the if statement like--> if(option2.equals(choice)){} just in case 
         else if("2".equals(choice)){
            System.out.println("Enter the User login of the Customer that you want to update there type:")
            String login = in.readLine();

            String query = String.format("UPDATE Users SET type = 'Manager' WHERE login = '%s'", login);
            esql.executeUpdate(query);
            System.out.println("Your Authority Type is now Changed!");
         }      
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }   
  }

  public static void SearchItem(Cafe esql){
   try{
      System.out.print("\t Please select the chiose of search for items by \"Name\" or \"Type\": ");
      String check = in.readLine();
      if("Name".equalsIgnoreCase(check)) {
        System.out.println("\tEnter The Item name you are searching: ");
        String item_name = in.readLine();
      }
      if("Type".equalsIgnoreCase(check)) {
        System.out.println("\tEnter The Item Type of what you are searching: ");
        String itemType = in.readLine();
      }
      String query = String.format("SELECT M.itemName, M.price FROM MENU M WHERE M.itemName = '%s' AND M.type '%s'", item_name, itemType);
      esql.executeQueryAndPrintResult(query);
     }catch(Exception e){
     System.err.println (e.getMessage ());
   }
  }

  public static void UpdateMenu(Cafe esql){
     System.out.print("\t Are you a manager? If so, provide your login");
     String check = in.readLine();
     String query = String.format("SELECT type FROM USER WHERE login ='%s' AND type = 'Manager'", check);
     if(query.isEmpty()){
        System.out.println("You are not a manager.");
     }
     else{
        System.out.println("Do you want to \"Add\", \"Delete\" or \"Update\" an item?");
        String edit = in.readLine();
        if("Add".equalsIgnoreCase(edit)){
         System.out.println("---Adding item---");
           System.out.println("Enter ItemName:");
           String ItemName = in.readLine();
           System.out.println("Enter Type:");
           String Type = in.readLine();
           System.out.println("Enter Price:");
           float Price = in.readLine();
           System.out.println("Enter Description:");
           String Description = in.readLine();
           System.out.println("Enter Image URL:");
           String URL = in.readLine();

           String query = String.format("INSERT INTO MENU (itemName,type,price,description,imageURL) VALUES('%s','%s','%f','%s','%s')",ItemName,Type,Price,Description,URL);
           esql.executeQueryAndPrintResult(query);

        }
        else if("Delete".equalsIgnoreCase(edit)){
           System.out.println("---Deleting item---");
           System.out.println("Enter ItemName that you want to delete:");
           String ItemName=in.readLine();
           String query = String.format("DELETE FROM MENU WHERE itemName='%s'",ItemName);
           esql.executeQuery(query);
        }
        else if("Update".equalsIgnoreCase(edit)){
           System.out.println("---Updating item---");
           System.out.println("Enter ItemName that you want to update:");
           String ItemName=in.readLine();
           System.out.println("What do you want to update, ItemName, Type, Price, Description or URL?");
           String input=in.readLine();
           if("ItemName".equalsIgnoreCase(input)){
              System.out.println("Enter New ItemName:");
              String newname=in.readLine();
              String query=String.format("UPDATE MENU SET itemName='%s' WHERE itemName='%s'",newname,ItemName);
              esql.executeUpdate(query);
           }
           else if("Type".equalsIgnoreCase(input)){
              System.out.println("Enter New Type:");
              String newtype=in.readLine();
              String query=String.format("UPDATE MENU SET type='%s' WHERE itemName='%s'",newtype,ItemName);
              esql.executeUpdate(query);
           }
           else if("Price".equalsIgnoreCase(input)){
              System.out.println("Enter New Price:");
              float newprice=in.readLine();
              String query=String.format("UPDATE MENU SET price='%f' WHERE itemName='%s'",newprice,ItemName);
              esql.executeUpdate(query);
           }
           else if("Description".equalsIgnoreCase(input)){
              System.out.println("Enter New Description:");
              String newdescription=in.readLine();
              String query=String.format("UPDATE MENU SET description='%s' WHERE itemName='%s'",newdescription,ItemName);
              esql.executeUpdate(query);
           }
           else if("URL".equalsIgnoreCase(input)){
              System.out.println("Enter New URL:");
              String newURL=in.readLine();
              String query=String.format("UPDATE MENU SET imageURL='%s' WHERE itemName='%s'",newURL,ItemName);
              esql.executeUpdate(query);
           }
        }
     }
  }

}//end Cafe

