class T{
int X,u,v,w,k,z,S,t[]=new int[3295],q[]=new int[512],M[]=t.clone(),o[][]=new int[3295][0];long H,L,A=1;
String s(boolean p,String[]I){
for(var L:I){var a=L.split("\\W+");u=L.charAt(0);t[z=i(a[v=63/u])*v]+=u&7;o[z]=new int[(w=a.length)+~v];for(u=v;++u<w;o[z][u+~v]=k|(t[k]+=8)/8<<12)X=(k=i(a[u]))==3084?z:X;}
for(k=1;p?k<1001:S+2<2<<t[X]/8;k++)for(u=v=0;u<=v;){z=q[u++];int x=z&4095,m=1<<(z>>12),y=t[x]&7;var h=z<0;H-=z>>31;L++;A*=h&x==X&&S<(S|=m)?k:1;if(h&y%6>2)continue;t[x]^=y/4;h=y<6?y>4:(M[x]=(M[x]|m)-(h?0:m))+2<2<<t[x]/8;for(int n:o[x])q[++v]=h?n|1<<31:n;}
return""+(p?H*(L-H):A);
}
int i(String s){return s.charAt(0)*26+(s+s).charAt(1);}
}
