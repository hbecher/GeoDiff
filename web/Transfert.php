<?php
//phpinfo();
if(isset($_POST['data']))#on s'assure de la receptioon des donnees
{
	$data = urldecode($_POST['data']);//data contient les differente chaine geojson separé par des /n
	//on recupere ces données dans un tableau pouis on les associe a different fichier .json
	$tab = explode("\n",$data);
	$data1=$tab[0];
	$data2=$tab[1];
	$data3=$tab[2];
	$data4=$tab[3];
	#echo "$data1<br>$data2<br>$data3";
	$uid = round(1000 * microtime(true));#on donne un id unique au fichier temporaire 
	$file = "/var/tmp/$uid.zip";
	$zip = new ZipArchive();
	$zip->open($file, ZipArchive::CREATE | ZipArchive::OVERWRITE);
	$zip->addFromString('modif.json',$data1);
	$zip->addFromString("ajout.json",$data2);
	$zip->addFromString("suppression.json",$data3);
	$zip->addFromString("diff.json",$data4);
	$zip->close();
	#remplissage header pour que le client puissent telecharger le zip 
	$size = filesize($file);
	header('Content-type: application/zip');
	header('Content-Disposition: attachment; filename='.$file);
	header('Content-Length: '.$size);
	header('Pragma: no-cache'); 
	header('Expires: 0'); 
	readfile($file);
}
?>