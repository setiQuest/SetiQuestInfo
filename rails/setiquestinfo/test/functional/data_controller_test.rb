require 'test_helper'

class DataControllerTest < ActionController::TestCase
  test "should get feeds" do
    get :feeds
    assert_response :success
  end

  test "should get sats" do
    get :sats
    assert_response :success
  end

end
