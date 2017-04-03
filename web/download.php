<?php
if($_SERVER['REQUEST_METHOD'] == 'POST')
{
	$diffs = [];
	$types = ['add', 'del', 'old', 'new', 'mod', 'id', 'undef'];

	foreach($types as $type)
	{
		if(isset($_POST[$type]) && !empty($_POST[$type]))
		{
			$diffs[$type] = urldecode($_POST[$type]);
		}
	}

	if(count($diffs) > 0)
	{
		$uid = round(1000 * microtime(true));
		$file = 'tmp/'.$uid.'.zip';

		$zip = new ZipArchive();

		$zip->open($file, ZipArchive::CREATE | ZipArchive::OVERWRITE);

		$all = json_decode("{\"type\":\"FeatureCollection\",\"features\":[]}", true);

		foreach($diffs as $type => $data)
		{
			$zip->addFromString('geodiff-'.$type.'.geojson', $data);

			$json = json_decode($data, true);

			foreach($json['features'] as $feature)
			{
				array_push($all['features'], $feature);
			}
		}

		$zip->addFromString('geodiff-all.geojson', json_encode($all));

		$zip->close();

		$size = filesize($file);

		header('Content-type: application/zip');
		header('Content-Disposition: attachment; filename=geodiff.zip');
		header('Content-Length: '.$size);
		header('Pragma: no-cache');
		header('Expires: 0');

		readfile($file);
	}
}

exit();
?>
