import java.util.*;

abstract class Node
{
	abstract boolean isLeaf();
}

class InteriorNode extends Node
{
	int attribute; // which attribute to divide on
	double pivot; // which value to divide on
	Node a;
	Node b;

	boolean isLeaf() { return false; }
}

class LeafNode extends Node
{
	double[] label;

	boolean isLeaf() { return true; }
}

class DecisionTree extends SupervisedLearner
{
	Node root;
    Random rand = new Random();
    String name()
    {
        return "DecisionTree";
    }
    
    int pickDivCol(Matrix features, Matrix labels)
    {
        int col = rand.nextInt(features.cols());
        return col;
    }

    double pickPivot(Matrix features, int divColomn)
    {
        int col = divColomn;
        int row = rand.nextInt(features.rows());
        double pivot = features.row(row)[col];
        return pivot;
    }

    Node buildTree(Matrix features, Matrix labels)
    {
        if(features.rows() != labels.rows())
        {
            System.out.println("Error!");
            return null;
        } 
        int divCol = pickDivCol(features, labels);
        double pivot = pickPivot(features, divCol);
        Matrix features_A = new Matrix();
        Matrix features_B = new Matrix();
        Matrix labels_A = new Matrix();
        Matrix labels_B = new Matrix();
        features_A.copyMetaData(features);
        features_B.copyMetaData(features);
        labels_A.copyMetaData(labels);
        labels_B.copyMetaData(labels);
        int featureType = features.valueCount(divCol); //the type of that colmn or attribute

        for(int i=0; i<features.rows(); i++)
        {
            if(featureType == 0)
            {
                if(feature.row(i)[divCol] < pivot)
                {
                    features_A.takeRow(features.removeRow(i));
                    labels_A.takeRow(labels.removeRow(i));
                }
                else
                {
                    features_B.takeRow(features.removeRow(i));
                    labels_B.takeRow(labels.removeRow(i));
                }
            }
            else
            {
                if(feature.row(i)[divCol] == pivot)
                {
                    
                }
                else
                {

                }
            }
        }

        return null;
    }
    void train(Matrix features, Matrix labels)
    {

    }

    void predict(double[] in, double[] out)
    {

    }
}
