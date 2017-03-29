
<?php
//phpinfo();
if(isset($_POST['data']))
{
	$data = urldecode($_POST['data']);
	#$data =$_POST['data'];
	#echo $data;
	$tab = explode("\n",$data);
	$data1=$tab[0];
	$data2=$tab[1];
	$data3=$tab[2];
	#echo "$data1<br>$data2<br>$data3";

	$uid = round(1000 * microtime(true));
	$uid = "YYY";
	$file = "/var/tmp/$uid.zip";#pb ici ???

	$zip = new ZipArchive();

	$zip->open($file, ZipArchive::CREATE | ZipArchive::OVERWRITE);
	$zip->addFromString('modif.json',$data1);
	$zip->addFromString("ajout.json",$data2);
	$zip->addFromString("suppression.json",$data3);
	#$zip->addFromString("diff.json",$data4);

	$zip->close();

	$size = filesize($file);

	header('Content-type: application/zip');
	header('Content-Disposition: attachment; filename='.$file);
	header('Content-Length: '.$size);
	header('Pragma: no-cache'); 
	header('Expires: 0'); 

	readfile($file);
}
?>
		