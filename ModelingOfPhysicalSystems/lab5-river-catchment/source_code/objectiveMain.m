tt = 1;
[optimal_tt, RMSE] = fminunc(@objective, tt);
optimal_tt(1)