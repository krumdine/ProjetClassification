#!/bin/bash

### alignement des séquences
i=0
for pool in Ressources/pool*.fasta
do
	nomFastaAln="Ressources/pool"$i".afa"
	echo Lancement de l alignement :
	Lib/muscle -in $pool -out $nomFastaAln
	i=$(($i + 1))
done
### arbre 
i=0
for pool in Ressources/pool*.afa
do
	nomTree='Ressources/pool'$i'.phy'
	echo Lancement de la phylogénie :
	Lib/muscle -maketree -in $pool -out $nomTree -cluster neighborjoining
	python src/arbre.py $nomTree 'Ressources/pool'$i'.xml'
	i=$(($i + 1))
done
exit 0
