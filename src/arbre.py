from Bio import Phylo
import os, sys

if __name__ == '__main__':
	Phylo.convert(sys.argv[1], 'newick', sys.argv[2], 'phyloxml')
