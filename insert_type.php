<?php


$db = mysql_connect("localhost", "root", "mysql*alan","menudata");
// Make sure to include your chosen username and password during MySQL installation
$selected = mysql_select_db("menudata",$db) 
  or die("Could not select examples");

// if (!$db) die('Could not connect' . mysql_error());
// echo 'Connected successfully';


if(isset($_GET['type'])){
	//echo "
	//			INSERT INTO 
 	//			cs_restaurant_type (restaurant_type_name) 
	//			VALUES (".$_GET['type'].")";
	
	mysql_query("
				INSERT INTO 
				cs_restaurant_type (restaurant_type_name) 
				VALUES ('".$_GET['type']."')");
}


?>

<form name="input" action="" method="get">
insert_desc: <input type="text" name="type">
<input type="submit" value="Submit">
</form>


<?php




$result=mysql_query('select * from cs_restaurant_type');
while ($row=mysql_fetch_row($result))
{
	echo $row[0].". <a href='http://localhost/~alan/insert_label.php?cs_restaurant_label=".$row[0]."'>".$row[1] . " </a></br>";


}

?>
