global S as
S = 1366;  % need to set global S and as to be able to 
as = 0.19; % reuse balance_equation function for all of use cases
Ts = 273;
Ta = 273;
Xp = [Ts Ta];
X = fsolve(@balance_equation, Xp);

X