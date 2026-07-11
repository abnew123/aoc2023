class M{
String s(boolean p,String I){long a=0;int e=p?0:1;for(var b:I.split("\n\n")){var g=b.split("\n");int r=g.length,c=g[0].length(),i,j,d,x;for(i=1;i<c;i++){x=0;for(d=0;i>d&&i+d<c;d++)for(j=0;j<r;)x+=g[j].charAt(i-d-1)!=g[j++].charAt(i+d)?1:0;if(x==e)a+=i;}for(i=1;i<r;i++){x=0;for(d=0;i>d&&i+d<r;d++)for(j=0;j<c;)x+=g[i-d-1].charAt(j)!=g[i+d].charAt(j++)?1:0;if(x==e)a+=100*i;}}return a+"";}
}
