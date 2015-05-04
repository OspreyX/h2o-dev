"""
A two dimensional table having row and column headers.
"""

import tabulate
import copy


class H2OTwoDimTable(object):
  """
  A class representing an 2D table (for pretty printing output).
  """
  def __init__(self, row_header=None, col_header=None, col_types=None,
             table_header=None, raw_cell_values=None,
             col_formats=None, cell_values=None):
    self.row_header = row_header
    self.col_header = col_header
    self.col_types = col_types
    self.table_header = table_header
    self.cell_values = cell_values if cell_values else self._parse_values(raw_cell_values, col_types)
    self.col_formats = col_formats

  def show(self, header=True):
    print
    if header: print self.table_header + ":"
    print
    table = copy.deepcopy(self.cell_values)
    nr = len(table)
    if nr > 20:
      print tabulate.tabulate(table[:5], headers=self.col_header, numalign="left", stralign="left")
      print "==="
      print tabulate.tabulate(table[(nr-5):], headers=self.col_header, numalign="left", stralign="left")
    else:
      print tabulate.tabulate(table, headers=self.col_header, numalign="left", stralign="left")
      print

  def __repr__(self):
    self.show()
    return ""

  def _parse_values(self, values, types):
    if self.col_header[0] is None:
      self.col_header = self.col_header[1:]
      types = types[1:]
      values = values[1:]
    for col_index, column in enumerate(values):
      for row_index, row_value in enumerate(column):
        if types[col_index] == 'integer':
          values[col_index][row_index]  = "" if row_value is None else int(float(row_value))

        elif types[col_index] in ['double', 'float', 'long']:
          values[col_index][row_index]  = "" if row_value is None else float(row_value)

        else:  # string?
          continue
    return zip(*values)  # transpose the values! <3 splat ops
