class O{
String s(boolean p,String[]I){var a=I[0].split(",");long r=0;if(p){for(var x:a)r+=h(x);return r+"";}var m=new java.util.LinkedHashMap();for(var x:a){int e=x.indexOf(61),q=e<0?x.length()-1:e;var l=x.substring(0,q);if(e<0)m.remove(l);else m.put(l,new Integer(x.substring(e+1)));}var z=new int[256];for(var e:m.keySet()){int b=h((String)e);r+=(b+1L)*++z[b]*(int)m.get(e);}return r+"";}
int h(String s){int v=0;for(var c:s.toCharArray())v=(v+c)*17%256;return v;}
}
