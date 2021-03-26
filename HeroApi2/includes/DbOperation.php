<?php
 
class DbOperation
{
    //Database connection link
    private $con;
    
    //Class constructor
    function __construct()
    {
        //Getting the DbConnect.php file
        require_once dirname(__FILE__) . '/DbConnect.php';
 
        //Creating a DbConnect object to connect to the database
        $db = new DbConnect();
 
        //Initializing our connection link of this class
        //by calling the method connect of DbConnect class
        $this->con = $db->connect();
    }
	
	/*
	* The create operation
	* When this method is called a new record is created in the database
	*/
	function createAccount($name, $email, $password){
		$stmt = $this->con->prepare("INSERT INTO user_module (User_name, User_email, Password) VALUES (?, ?, ?)");
		$stmt->bind_param("sss", $name, $email, $password);
		if($stmt->execute())
			return true; 
		return false; 
	}

	/*define('DB_HOST', 'localhost');
	define('DB_USER', 'localhostnew');
	define('DB_PASS', 'Muruli143@');
	define('DB_NAME', '3dpilot');*/

	//check username 
	function checkUser($name){
        //$link = mysqli_connect('localhost', 'root', '', '3dcopilot');

		$link = mysqli_connect('localhost', 'localhostnew', 'Muruli143@', '3dpilot');
		$sql = "SELECT count(*) as num FROM user_module WHERE User_name LIKE '$name'";
		if($result = mysqli_query($link, $sql)){
           if(mysqli_num_rows($result) > 0){
		      while($row = mysqli_fetch_array($result)){
				 if($row['num']>0)
				  {
					 return true;
				  }
				  else{
					  return false;
				  }
			  }
		   }
		  
		}
	}

	//check email 
	function checkEmail($name){
        //$link = mysqli_connect('localhost', 'root', '', '3dcopilot');

		$link = mysqli_connect('localhost', 'localhostnew', 'Muruli143@', '3dpilot');
		$sql = "SELECT count(*) as num FROM user_module WHERE User_email LIKE '$name'";
		if($result = mysqli_query($link, $sql)){
           if(mysqli_num_rows($result) > 0){
		      while($row = mysqli_fetch_array($result)){
				 if($row['num']>0)
				  {
					 return true;
				  }
				  else{
					  return false;
				  }
			  }
		   }
		  
		}
		
			
	}

	//logins
	function checkLogin($name, $password){
		$stmt = $this->con->prepare("SELECT User_id, User_name, User_email, Password, Avatar FROM user_module WHERE User_name=? AND Password=? OR User_email=? AND Password=? LIMIT 1");
        $stmt->bind_param('ssss', $name, $password, $name, $password);
        $stmt->execute();
		$stmt->bind_result($id, $username, $email, $password, $avatar);
		$users = array(); 
		
		while($stmt->fetch()){
			$log  = array();
			$log['User_id'] = $id; 
			$log['User_name'] = $name; 
			$log['User_email'] = $email; 
			$log['Password'] = $password;
			$log['Avatar'] = $avatar;
			
			array_push($users, $log); 
		}
		return $users;

		//if($stmt->execute())
		//	return true; 
		//return false; 
	}

	/*
	* The read operation
	* When this method is called it is returning all the existing record of the database
	*/
	function getHeroes(){
		$stmt = $this->con->prepare("SELECT upload_table.No_of_models, upload_table.Model_Description, upload_table.Model_path, user_module.Avatar, user_module.User_name 
		                             FROM upload_table 
									 JOIN user_module ON user_module.User_id = upload_table.User_id
									 WHERE upload_table.File_ext LIKE 'mp4'");
		$stmt->execute();
		$stmt->bind_result($id, $name, $realname, $rating, $username);
		
		$heroes = array(); 
		
		while($stmt->fetch()){
			$hero  = array();
			$hero['id'] = $id; 
			$hero['name'] = $name; 
			$hero['realname'] = $realname; 
			$hero['rating'] = $rating;
			$hero['username'] = $username;
			
			array_push($heroes, $hero); 
		}
		
		return $heroes; 
	}
	
	/*
	* The update operation
	* When this method is called the record with the given id is updated with the new given values
	*/
	function updateHero($id, $name, $realname, $rating, $teamaffiliation){
		$stmt = $this->con->prepare("UPDATE heroes SET name = ?, realname = ?, rating = ?, teamaffiliation = ? WHERE id = ?");
		$stmt->bind_param("ssisi", $name, $realname, $rating, $teamaffiliation, $id);
		if($stmt->execute())
			return true; 
		return false; 
	}
	
	
	/*
	* The delete operation
	* When this method is called record is deleted for the given id 
	*/
	function deleteHero($id){
		$stmt = $this->con->prepare("DELETE FROM heroes WHERE id = ? ");
		$stmt->bind_param("i", $id);
		if($stmt->execute())
			return true; 
		
		return false; 
	}
}