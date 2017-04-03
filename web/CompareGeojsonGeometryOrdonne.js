//https://forum.alsacreations.com/topic-5-3955-1-Rsolu-Comparer-deux-arrays-.html 
 //VERSION QUI TIENT COMPTE DE L'ORDRE 

    function CompareGeojsonGeometry(a1, a2,ty)  
{
     
    var res = false ;
    //console.log(ty);
    switch(ty){
        case "Point"://{longitude,latitude,(hauteur)}
        //verif des coordonnees des point et du param optionnel hauteur 
            if (a1.length !== a2.length){ //cas ou la hauteur est present sur l'un des deux point si non presnte on suppose = 0 
                return ( a1[0] == a2[0] && a1[1] == a2[1] && ( a1[2] == 0 || a1[2] == "undefined") && ( a2[2] == 0 || a2[2] == "undefined"));
            }
            else{
                return (a1[0]==a2[0] && a1[1]==a2[1] && ( a1[2]==a2[2] ) );//hauteur optionnel
            }
        case "MultiPoint"://{point1,point2,...}
        case "LineString"://On suppose que les linestrings contiennent au moins 2 points 
        //multipoint et linestring cette version va tenir compte de l'ordre 
            if (a1.length !== a2.length){//nb de point dans les multipoint 
                    return false;//forcement faux 
            }else{
                //cas particulier non traite : cycle {p1,p2,p3} != {p2,p3,p1} pourrai etre attendu vraie mais reconnu faux par cette fonction 
                for (var i = 0; i < a1.length; ++i){
                    //console.log("a=",a);
                    //pour chaque  ieme elem des des linestring ou multi point : des points 
                    res= CompareGeojsonGeometry(a1[i], a2[i],"Point");
                    if(!res){
                        return false;
                    }
                }
                return true ;
            }
        
        case "MultiLineString":
        case "Polygon":
            if (a1.length !== a2.length){//nb de linestring dans les multilinestrings ou dans les polygones
                    return false;//forcement faux 
            }else{
                for (var i = 0; i < a1.length; ++i){
                    //console.log("a=",a);
                    //pour chaque  ieme elem :des linestrings
                    res= CompareGeojsonGeometry(a1[i], a2[i],"LineString");
                    if(!res){
                        return false;
                    }
                }
                return true ;
            }
        case "MultiPolygon":
            if (a1.length !== a2.length){//nb de polygone
                    return false;//forcement faux 
            }else{
                for (var i = 0; i < a1.length; ++i){
                    //console.log("a=",a);
                    //pour chaque  ieme elem : polygon
                    res= CompareGeojsonGeometry(a1[i], a2[i],"Polygon");
                    if(!res){
                        return false;
                    }
                }
                return true ;
            }
        }
}
