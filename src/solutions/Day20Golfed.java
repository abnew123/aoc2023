import java.util.*;class T{
int R,X,u,v,k,z,S,t[]=new int[677],q[]=new int[512],M[]=t.clone(),o[][]=new int[677][0];long H,L,A=1;Map Z=new HashMap();
String s(boolean p,String[]I){
i("rx");for(var L:I){var a=L.split("\\W+");u=L.charAt(0);t[z=i(a[v=63/u])]+=u&7;R=v<1?z:R;o[z]=new int[a.length+~v];for(u=v;++u<a.length;){if((k=i(a[u]))<1)X=z;o[z][u+~v]=k|(t[k]+=8)/8<<10;}}
for(k=1;p?k<1001:S+2<2<<t[X]/8;k++)for(q[u=0]=R,v=1;u<v;){z=q[u++];int x=z&1023,m=1<<(z>>10),y=t[x]&7;var h=z<0;H-=z>>31;L++;if(h&x==X&&S<(S|=m))A*=k;if(y<6){if(h&y>2)continue;t[x]^=y/4;h=y>4;}else h=(M[x]=(M[x]|m)-(h?0:m))+2<2<<t[x]/8;for(int n:o[x])q[v++]=h?n|1<<31:n;}
return""+(p?H*(L-H):A);
}
int i(String s){return(int)Z.computeIfAbsent(s,k->Z.size());}
}
