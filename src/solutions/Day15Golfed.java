class O{
String s(boolean p,String[]I){long r=0;var m=new java.util.LinkedHashMap();for(var x:I[0].split(","))if(p)r+=h(x);else{var e=x.charAt(x.length()-1)-48;var l=x.split("\\W")[0];if(e<0)m.remove(l);else m.put(l,e);}var z=new int[256];if(!p)for(var e:m.keySet()){int b=h(e+"");r+=(b+1L)*++z[b]*(int)m.get(e);}return r+"";}
int h(String s){return s.chars().reduce(0,(v,c)->(v+c)*17%256);}
}
