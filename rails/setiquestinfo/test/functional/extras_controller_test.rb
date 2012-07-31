require 'test_helper'

class ExtrasControllerTest < ActionController::TestCase
  test "should get unl_radec_animation" do
    get :unl_radec_animation
    assert_response :success
  end

end
