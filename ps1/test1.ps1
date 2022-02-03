
$trainPath
$validPath
$testPath
[float]$gamma
[int]$topk
[int]$d
[int]$SizeOfGroup
$chosenDataset
$isTest
$configPath = ".\ps1\"


javac Main.java


for ($i = 0; $i -lt $args.count; $i++){
    switch ($i){
        0 {$chosenDataset = $args[0]; break}
        1 {$isTest = $args[1]; break}
        Default {Write-Host "?"}
    }
}

$configFile = Get-ChildItem -Path $configPath -Name | Where-Object{$_ -match $chosenDataset}

$configFile = -join ($configPath, $configFile)

$config = Get-Content -Path $configFile

foreach ($curLine in $config){
    $words = -Split $curLine
    switch ($words[0]) {
        "n:" { $n = $words[1] ; break}
        "m:" { $m = $words[1] ; break}
        "gamma:" { $gamma = $words[1]; break}
        "topk:" {$topk = $words[1]; break}
        "d:" {$d = $words[1]; break}
        "SizeOfGroup:" {$SizeOfGroup = $words[1]; break}
        Default {}
    }
}

$fns = Get-childItem -Path ".\Data-S-FMSM" -Name -recurse | Where-Object{$_ -match "$chosenDataset"}

foreach ($curPath in $fns) {
    If ( $curPath -match "train" ){
        $trainPath = $curPath
    }
    If ( $curPath -match "valid"){
        $validPath = $curPath
    }
    If ( $curPath -match "test"){
        $testPath = $curPath
    }
}  

$trainPath = -join (".\Data-S-FMSM\", $trainPath)
$validPath = -join (".\Data-S-FMSM\", $validPath)

$cnt=0
$jobs =@()
$jobInrun = @()

foreach ( $re in 0.001,0.01,0.1 ){

    foreach ( $l in 1,2,3 ){

        foreach ( $t in 100,500,1000 ){

            
            $curStore = -join ("./out/",$chosenDataset,"/train_RT",$re,"_L",$l,"_T",$t,".csv")
            java Main -fnTrainData $trainPath -fnTestData $validPath -n $n -m $m -alpha_w $re -alpha_v $re -beta_v $re -beta_eta $re -L $l -gamma $gamma -T $t -topK $topk -d $d >$curStore
            
        }
    }
    
}



    


