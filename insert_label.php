<?php


$db = mysql_connect("localhost", "root", "mysql*alan","menudata");
// Make sure to include your chosen username and password during MySQL installation
$selected = mysql_select_db("menudata",$db) 
  or die("Could not select examples");


if(isset($_GET['label'])){
	mysql_query("
				INSERT INTO 
				cs_restaurant_label (cs_label_name, restaurant_type_id) 
				VALUES ('".$_GET['label']."',".$_GET['cs_restaurant_label'].")");
}


?>
<a href='http://localhost/~alan/insert_type.php'>BACK</a>

<form name="input" action="" method="get">
insert_desc: <input type="text" name="label">
<input type="hidden" name="cs_restaurant_label" value=<?php echo $_GET['cs_restaurant_label'];?>>
<input type="submit" value="Submit">
</form>


<?php




$result=mysql_query('select * from cs_restaurant_label where restaurant_type_id='.$_GET['cs_restaurant_label']);
while ($row=mysql_fetch_row($result))
{
	echo $row[0].". <a href='http://localhost/~alan/insert_desc.php?cs_desc=".$row[0]."'>".$row[1] . " </a></br>";


}


?>


