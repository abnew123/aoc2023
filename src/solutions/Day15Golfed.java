class O{
String s(boolean p,String I){var a=I.replaceAll("\\R","").split(",");long r=0;if(p){for(var x:a)r+=h(x);return r+"";}var b=new java.util.LinkedHashMap[256];for(int i=0;i<256;i++)b[i]=new java.util.LinkedHashMap();for(var x:a){int e=x.indexOf(61),q=e<0?x.length()-1:e;var l=x.substring(0,q);var m=b[h(l)];if(e<0)m.remove(l);else m.put(l,Integer.valueOf(x.substring(e+1)));}for(int i=0;i<256;i++){int j=0;for(var v:b[i].values())r+=(i+1L)*++j*(int)v;}return r+"";}
int h(String s){int v=0;for(char c:s.toCharArray())v=(v+c)*17%256;return v;}
}
