class O{
String s(boolean p,String[]I){int r=0;var m=new java.util.LinkedHashMap();for(var x:I[0].split(","))if(p)r+=h(x);else{var q=x.split("[=-]");if(q.length<2)m.remove(q[0]);else m.put(q[0],new Integer(q[1]));}var z=new int[256];if(!p)for(var e:m.keySet()){int b=h(e+"");r+=(b+1)*++z[b]*(int)m.get(e);}return r+"";}
int h(String s){return s.chars().reduce(0,(v,c)->(v+c)*17%256);}
}
