class ExtrasController < ApplicationController

  before_filter :authenticate

  def observation_reason
  end

  protected
    def authenticate
      authenticate_or_request_with_http_basic do |username, password|
      username == "admin" && password == "test"
    end
  end

end
