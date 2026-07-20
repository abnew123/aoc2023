class W{
int D[],g[],L;String G;boolean P;
String s(boolean p,String[]Z){P=p;G=String.join("\n",Z);int w=G.indexOf(10)+1,N=0,h[]=new int[L=G.length()];D=new int[]{1,w,-1,-w};
for(int v=L,n;v-->0;)if(o(v)){n=0;for(int d:D)if(o(v+d))n++;if(n!=2)h[v]=N+=5;}
g=new int[N];
for(int v=L,n;v-->0;)if(h[v]>0)for(int d=4;d-->0;)if(a(G.charAt(v),d))for(int f=h[v]-5,x=v,t=d,s=0;;){if(!o(x+=D[t]))break;s++;if(h[x]>0){g[f+d]=(h[x]/5-1)*L+s;break;}n=4;while(n-->0&&!(a(G.charAt(x),n)&o(x+D[n])&n!=(t^2)));if(n<0)break;t=n;}
return""+d(h[G.indexOf(46)]-5);
}
int d(int a){if(a<1)return 0;g[a+4]=1;int b=-L;for(int z=4,q;z-->0;)if((q=g[a+z])>0&g[q/L*5+4]<1)b=Math.max(b,q%L+d(q/L*5));g[a+4]=0;return b;}
boolean a(char c,int d){return!P|c<47|c==">v<^".charAt(d);}
boolean o(int v){return v>=0&v<L&&G.charAt(v)>35;}
}
