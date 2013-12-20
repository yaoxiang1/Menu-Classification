<?php


$db = mysql_connect("localhost", "root", "mysql*alan","menudata");
// Make sure to include your chosen username and password during MySQL installation
$selected = mysql_select_db("menudata",$db) 
  or die("Could not select examples");


if(isset($_GET['desc'])){
	mysql_query("
				INSERT INTO 
				cs_restaurant_label_desc (cs_desc_value, cs_label_id) 
				VALUES ('".$_GET['desc']."',".$_GET['cs_desc'].")");
}


?>
<a href='http://localhost/~alan/insert_type.php'>BACK</a>

<form name="input" action="" method="get">
insert_desc: <input type="text" name="desc">
<input type="hidden" name="cs_desc" value=<?php echo $_GET['cs_desc'];?>>
<input type="submit" value="Submit">
</form>


<?php

$result=mysql_query('
		SELECT * 
		FROM cs_restaurant_label_desc 
		WHERE cs_label_id='.$_GET['cs_desc']);
while ($row=mysql_fetch_row($result))
{
	echo $row[0].".".$row[1]."</br>";
}

?>
