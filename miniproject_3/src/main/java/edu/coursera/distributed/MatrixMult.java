package edu.coursera.distributed;

import edu.coursera.distributed.util.MPI;
import edu.coursera.distributed.util.MPI.MPIException;
import edu.coursera.distributed.util.MPI.MPI_Request;

/**
 * A wrapper class for a parallel, MPI-based matrix multiply implementation.
 */
public class MatrixMult {
    /**
     * A parallel implementation of matrix multiply using MPI to express SPMD
     * parallelism. In particular, this method should store the output of
     * multiplying the matrices a and b into the matrix c.
     *
     * This method is called simultaneously by all MPI ranks in a running MPI
     * program. For simplicity MPI_Init has already been called, and
     * MPI_Finalize should not be called in parallelMatrixMultiply.
     *
     * On entry to parallelMatrixMultiply, the following will be true of a, b,
     * and c:
     *
     *   1) The matrix a will only be filled with the input values on MPI rank
     *      zero. Matrix a on all other ranks will be empty (initialized to all
     *      zeros).
     *   2) Likewise, the matrix b will only be filled with input values on MPI
     *      rank zero. Matrix b on all other ranks will be empty (initialized to
     *      all zeros).
     *   3) Matrix c will be initialized to all zeros on all ranks.
     *
     * Upon returning from parallelMatrixMultiply, the following must be true:
     *
     *   1) On rank zero, matrix c must be filled with the final output of the
     *      full matrix multiplication. The contents of matrix c on all other
     *      ranks are ignored.
     *
     * Therefore, it is the responsibility of this method to distribute the
     * input data in a and b across all MPI ranks for maximal parallelism,
     * perform the matrix multiply in parallel, and finally collect the output
     * data in c from all ranks back to the zeroth rank. You may use any of the
     * MPI APIs provided in the mpi object to accomplish this.
     *
     * A reference sequential implementation is provided below, demonstrating
     * the use of the Matrix class's APIs.
     *
     * @param a Input matrix
     * @param b Input matrix
     * @param c Output matrix
     * @param mpi MPI object supporting MPI APIs
     * @throws MPIException On MPI error. It is not expected that your
     *                      implementation should throw any MPI errors during
     *                      normal operation.
     */
    public static void parallelMatrixMultiply(Matrix a, Matrix b, Matrix c,
            final MPI mpi) throws MPIException {
    	
    	//mpi.MPI_Init();
    	final int nrows = c.getNRows();
    	final int rank = mpi.MPI_Comm_rank(mpi.MPI_COMM_WORLD);
    	final int size = mpi.MPI_Comm_size(mpi.MPI_COMM_WORLD);
        final int chunk = (c.getNRows() + size -1)/size;
        final int stRow = rank * chunk;
        int endRow = (rank+1) * chunk;
        if(endRow > nrows) endRow = nrows;
        
        mpi.MPI_Bcast(a.getValues(),0 , a.getNRows() * a.getNCols(),0, mpi.MPI_COMM_WORLD);
        mpi.MPI_Bcast(b.getValues(), 0, b.getNRows()*b.getNCols(),0, mpi.MPI_COMM_WORLD);
        for (int i = stRow; i < endRow; i++) {
            for (int j = 0; j < c.getNCols(); j++) {
                c.set(i, j, 0.0);

                for (int k = 0; k < b.getNRows(); k++) {
                    c.incr(i, j, a.get(i, k) * b.get(k, j));
                }
            }
        }
        
        if(rank == 0) {
           MPI_Request[] request = new MPI_Request[size-1];	
           for(int i = 1 ; i< size ; i++) {	
        	   final int offset = i * chunk;
        	   int erow = (i+1) * chunk;
        	   if(erow >  nrows) erow = nrows ;
        	   
        	   final int posOffset = offset * c.getNCols();
        	   final int elementsSize =  (erow - offset) * c.getNCols();
        	   
        	   request[i-1] = mpi.MPI_Irecv(c.getValues(), 
        			   posOffset,
        			   elementsSize, 
        			   i, i, mpi.MPI_COMM_WORLD);
           }
           mpi.MPI_Waitall(request);
        }else {
           mpi.MPI_Isend(c.getValues(), stRow*c.getNCols(), (endRow - stRow) * c.getNCols(), 0, rank , mpi.MPI_COMM_WORLD);
          	
        }
    }
}
