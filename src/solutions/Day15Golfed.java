class O{
int r;
String s(boolean p,String[]I){var m=new java.util.LinkedHashMap();for(var x:I[0].split(","))if(p)r+=h(x);else{var q=x.split("[=-]");m.compute(q[0],(K,V)->q.length<2?null:new Integer(q[1]));}var z=new int[256];m.forEach((k,v)->r+=-~h(k)*++z[h(k)]*(int)v);return r+"";}
int h(Object s){return(s+"").chars().reduce(0,(v,c)->(v+c)*17%256);}
}
