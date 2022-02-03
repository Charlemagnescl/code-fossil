from typing import Sized
import pandas as pd
import numpy as np
import os

def main():
    cDir = os.getcwd()

    data = []
    test = []

    for root, dirs, files in os.walk(cDir):
        for file in files:
            if not file.endswith('.txt'):
                continue
            
            cpath = os.path.join(cDir, file)
            rt = 0.0
            T = 0
            L = 0
            Prec = 0.0
            Rec = 0.0
            F1 = 0.0
            NDCG = 0.0
            call1 = 0.0
            timeCount = 0
            SizeOfGourp = 0
            with open(cpath,'r') as f:
                for line in f:
                    if line.count('[') != 0:
                        continue
                    li = line.split(':')
                    li[1] = li[1].rstrip('\n')
                   
                    
                    if li[0] == 'alpha_w'  \
                        or li[0] == 'alpha_v' \
                        or li[0] == 'beta_eta' \
                        or li[0] == 'beta_v' :
                        rt = float(li[1])
                    if li[0] == 'T':
                        T = int(li[1])
                    if li[0] == 'L':
                        L = int(li[1])
                    if li[0] == 'SizeOfGroup':
                        SizeOfGourp = int(li[1])
                    if li[0] == 'Prec@20':
                        Prec = float(li[1])
                    if li[0] == 'Rec@20':
                        Rec = float(li[1])
                    if li[0] == 'F1@20':
                        F1 = float(li[1])
                    if li[0] == 'NDCG@20':
                        NDCG = float(li[1])
                    if li[0] == '1-call@20':
                        call1 = float(li[1])
                    if li[0] == 'Time consuming':
                        timeCount = int(li[1].rstrip(' s'))
                if  file.count('test') == 0:
                    data.append({ 'T':T, 'rt':rt, 'L':L, 'SizeOfGroup':SizeOfGourp, 'Prec@20':Prec, 'Rec@20':Rec, 'F1@20':F1, 'NDCG@20':NDCG, '1-call@20':call1, 'Time consuming':timeCount})
                else :
                    test.append({ 'T':T, 'rt':rt, 'L':L, 'SizeOfGroup':SizeOfGourp, 'Prec@20':Prec, 'Rec@20':Rec, 'F1@20':F1, 'NDCG@20':NDCG, '1-call@20':call1, 'Time consuming':timeCount})

    #print(data)
    Data = pd.DataFrame(data)
    Data = Data.sort_values(by=['L','rt','T'],ascending=True)
    Data.to_csv(os.path.join(cDir,'out.csv'), index=False)

    if len(test) != 0:
        Test = pd.DataFrame(test)
        Test = Test.sort_values(by=['L','rt','T'],ascending=True)
        Test.to_csv(os.path.join(cDir,'testout.csv'), index=False)

if __name__ == "__main__":
    main()
