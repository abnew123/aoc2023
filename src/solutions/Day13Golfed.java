class M{
String s(boolean p,String[]I){long a=0;int e=p?0:1;for(var b:String.join("\n",I).split("\n\n")){int c=(b+"\n").indexOf(10),r=(b.length()+1)/(c+1),i,j,d,x;for(int q=2;q-->0;){int n=q<1?c:r,m=q<1?r:c,z=q<1?1:c+1;for(i=n;i-->1;){x=0;for(d=0;i>d&i+d<n;d++)for(j=m;j-->0;)x+=b.charAt((i-d-1)*z+j*(c+2-z))!=b.charAt((i+d)*z+j*(c+2-z))?1:0;if(x==e)a+=(q<1?1:100)*i;}}}return a+"";}
}
