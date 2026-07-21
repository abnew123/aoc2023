class W{
int g[],L;byte[]G;boolean P;
String s(boolean p,String[]Z){P=p;G=String.join("\n",Z).getBytes();int N=0,h[]=new int[L=G.length],w=(L+1)/Z.length,v,D[]={1,w,-1,-w};
for(v=L;v-->0;)if(o(v)){w=0;for(int d:D)if(o(v+d))w++;if(w!=2)h[v]=N+=5;}
g=new int[N+5];
for(v=L;v-->0;)if(h[v]>0)for(int e=4;e-->0;)if(a(G[v],e))for(int x=v,t=e,s=0;t>=0&&o(x+=D[t]);t=w){s++;if(h[x]>0){g[h[v]-~e]=h[x]*L+s;break;}w=4;while(w-->0&&!(a(G[x],w)&o(x+D[w])&w!=(t^2)));}
return""+d(N);
}
int d(int a){if(a<6)return 0;g[a]=1;int b=-L;for(int z=5,q;z-->1;)if((q=g[a+z])>0&g[q/L]<1)b=Math.max(b,q%L+d(q/L));g[a]=0;return b;}
boolean a(byte c,int d){return!P|c<47|c==">v<^".charAt(d);}
boolean o(int v){return v>=0&v<L&&G[v]>35;}
}
