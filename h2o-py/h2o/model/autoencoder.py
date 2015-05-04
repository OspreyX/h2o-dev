"""
AutoEncoder Models
"""

from model_base import *
from metrics_base import *

class H2OAutoEncoderModel(ModelBase):
  """
  Class for AutoEncoder models.
  """
  def __init__(self, dest_key, model_json):
    super(H2OAutoEncoderModel, self).__init__(dest_key, model_json,H2OAutoEncoderModelMetrics)

  def anomaly(self,test_data):
    """
    Obtain the reconstruction error for the input test_data.

    :param test_data: The dataset upon which the reconstruction error is computed.
    :return: Return the reconstruction error.
    """
    if not test_data: raise ValueError("Must specify test data")
    # cbind the test_data vecs together and produce a temp key
    test_data_key = H2OFrame.send_frame(test_data)
    # get the anomaly
    j = H2OConnection.post_json("Predictions/models/" + self._key + "/frames/" + test_data_key, reconstruction_error=True)
    # extract the frame data
    anomaly_frame_key = j["model_metrics"][0]["predictions"]["key"]["name"]
    anomaly_frame_meta = h2o.frame(anomaly_frame_key)["frames"][0]
    # create vecs by extracting vec_keys, col length, and col names
    vec_keys = anomaly_frame_meta["vec_keys"]
    rows = anomaly_frame_meta["rows"]
    cols = [col["label"] for col in anomaly_frame_meta["columns"]]
    vecs = H2OVec.new_vecs(zip(cols, vec_keys), rows)
    # remove test_data shallow key
    h2o.delete(test_data_key)
    # return new H2OFrame object
    return H2OFrame(vecs=vecs)