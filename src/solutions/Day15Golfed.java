class O{
String s(boolean p,String I){var p0=I.split(",");long a=0;if(p){for(var s:p0)a+=h(s);return a+"";}var n=new String[256][p0.length];var v=new int[256][p0.length];var z=new int[256];for(var s:p0){int e=s.indexOf(61),q=e<0?s.length()-1:e,b=h(s.substring(0,q)),i=0;var l=s.substring(0,q);for(;i<z[b]&&!l.equals(n[b][i]);i++);if(e<0){if(i<z[b])for(z[b]--;i<z[b];i++){n[b][i]=n[b][i+1];v[b][i]=v[b][i+1];}}else{if(i==z[b])n[b][z[b]++]=l;v[b][i]=s.charAt(e+1)-48;}}for(int i=0;i<256;i++)for(int j=0;j<z[i];j++)a+=(i+1L)*(j+1)*v[i][j];return a+"";}
int h(String s){int v=0;for(char c:s.toCharArray())v=(v+c)*17%256;return v;}
}
