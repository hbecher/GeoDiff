//https://forum.alsacreations.com/topic-5-3955-1-Rsolu-Comparer-deux-arrays-.html
 //revoir la fonction ci dessous 
    function CompareGeojsonGeometry(a1, a2,ty)
    //to do eventuelement s'aafranchir du parametre ty 
    
{
    //n = 1 indique de s'assurer que le premier element est identique dans a1 et a2 
    var res = false ;//resultat intermediaire 1
    var subres = true;
    if ( a1.type != a2.type ) {
        return false;//forcement faux 
    } 
    else {
        switch(ty){

        case "Point":
        //verif des coordonnees des point && du param optionnel hauteur 
            if (a1.length !== a2.length){ //cas ou la hautteur est present sur l'un des deux point si non presnte on suppose = 0 
                return ( a1[0] == a2[0] && a1[1] == a2[1] && ( a1[2] == 0 || a1[2] == "undefined") && ( a2[2] == 0 || a2[2] == "undefined"));
            }
            else{
                return (a1[0]==a2[0] && a1[1]==a2[1] && ( a1[2]==a2[2] ) );//hauteur optionnel
            }
        case "MultiPoint":
        //a condenser 
            if (a1.length !== a2.length){//nb de point dans les multipoint 
                    return false;//forcement faux 
            }else{
                for (var a = 0; a < a1.length; ++a){
                //pour chaque elem du premier tab
                    res = false;
                    for ( var b = 0; b < a2.length; ++b){
                        //console.log("b=",b);
                        res = res || CompareGeojsonGeometry(a1[a], a2[b],"Point");
                        //console.log("res=",res);
                       if (res){
                             break; 
                       }
                    }
                    if(!res){
                        return false;
                    }
                }
                return true ;
            }
        case "LineString":
            res=false;
            if (a1.length !== a2.length || a1.length < 2 ){//nb de point dans les linstring
                    return false;//forcement faux 
            }else{
                for (var a = 0; a < a1.length; ++a){
                    res = false;
                    //console.log(" debut for res= ",res," a=",a);
                    
                    res = CompareGeojsonGeometry(a1[a], a2[a],"Point");//comparaison des point dans l'ordre 
                    //console.log(" debut for res= ",res," a=",a);
                    if(!res){
                        return false;
                    }
                }
                return true;
            }
        case "MultiLineString":
            if (a1.length !== a2.length){//nb de linestring diff
                return false;
            }
            else{
                  for (var a = 0; a < a1.length; ++a){
                    //console.log("a=",a);
                 //pour chaque elem du premier tab
                    res = false;
                    for ( var b = 0; b < a2.length; ++b){
                        //console.log("b=",b);
                        res = res || CompareGeojsonGeometry(a1[a], a2[b],"LineString");
                        //res = CompareGeojsonGeometry(a1[a], a2[b],"LineString");
                        //attenion cas ligne dex point ordre non important ? 
                        //console.log("res=",res);
                       if (res){
                             break; 
                       }
                    
                    }
                    if(!res){
                        return false;
                    }
                    // return CompareGeojsonGeometry(a1[a], a2[a]) || CompareGeojsonGeometry(a1[a], a2[2] || ... || CompareGeojsonGeometry(a1[a], a2[dernier]
                }
                return true ;

            }
            break;//pas necessaire
        case "Polygon":
            var elm0_identique = CompareGeojsonGeometry(a1[0], a2[0],"LineString");//a implanter 
            if (a1.length !== a2.length || !elm0_identique){//nb de linestringring(linestring 4p ou plus et premier == dernier )
                return false;
            }
            else{
                //premier = dernier ??
                for (var a = 1; a < a1.length; ++a){
                    //console.log("a=",a);
                //pour chaque elem du premier tab
                    res = false;
                    for ( var b = 1; b < a2.length; ++b){
                        //console.log("b=",b);
                        res = res || CompareGeojsonGeometry(a1[a], a2[b],"LineString");//comparaison linestring

                       // console.log("res=",res);
                       if (res){
                             break; 
                       }
                    
                    }
                    if(!res){//si premier different du denier point dans la linestring considere 
                        //console.log("prems diff du der ")
                        return false;
                    }
                }
                return true ;
            }

        case "MultiPolygon":
            if (a1.length !== a2.length ){//nb de polygone
                return false;
            }
            else{
                for (var a = 0; a < a1.length; ++a){
                     //console.log("a=",a);
                    //pour chaque elem du premier tab
                        res = false;
                        for ( var b = 0; b < a2.length; ++b){
                            //console.log("b=",b);
                            res = res || CompareGeojsonGeometry(a1[a], a2[b],"Polygon");//comparaison polygon
                            //console.log("res=",res);
                        if (res){
                             break; 
                        }
                    
                        }
                        if(!res){//si premier different du denier point dans la linestring considere 
                            return false;
                        }
                }
                return true ;
            }
            break;//pas necessaire

            }
        }
    //}
}