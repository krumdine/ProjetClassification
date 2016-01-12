#!/bin/bash

### alignement des séquences
i=0
for pool in pool*.fasta
do
	nomFastaAln="pool"$i".afa"
	echo Lancement de l alignement :
	/net/cremi/nodcosta001/test_dataMining/muscle -in $pool -out $nomFastaAln
	i=$(($i + 1))
done
### arbre 
i=0
for pool in pool*.afa
do
	nomTree='pool'$i'.phy'
	echo Lancement de la phylogénie :
	/net/cremi/nodcosta001/test_dataMining/muscle -maketree -in $pool -out $nomTree -cluster neighborjoining
	python arbre.py $nomTree 'pool'$i'.xml'
	i=$(($i + 1))
done
exit 0
