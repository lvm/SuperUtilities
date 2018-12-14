/*
References:
 https://unitus.org/FULL/12tone.pdf
*/

Serialism {
  classvar row, col, matrix;

  new {}

  *initClass {
    /*
    first we create a random 12 tone array.
    this time we move from left to right.
    */
    row = (0..11).scramble;
    /*
    then, we _subtract_ each item from `row` to 12, then we apply modulo to keep it between from 0 to 11.
    that is something like:
    for i in range(0,12):
      col[i] = 12 - row
      while col[i] >= 12:
        col[i] = col[i] - 12

    note that this time, we're moving from top to bottom.
    */
    col = (12 - row) % 12;
    /*
    and now, for something complet--
    we _sum_ each item from `col` to row and once again, we need to apply modulo.
    */
    matrix = col.collect((_ + row)%12);
  }

  get {
    ^matrix;
  }

}
