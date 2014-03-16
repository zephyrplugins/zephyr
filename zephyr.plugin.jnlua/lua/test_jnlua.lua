local Clock = java.require("zephyr.plugin.core.api.synchronization.Clock")
local Zephyr = java.require("zephyr.plugin.core.api.Zephyr")
local class = require 'pl.class'
local out = java.require("java.lang.System").out

local Sub = class()
function Sub:_init()
  self.sub = 0
end
function Sub:update()
  self.sub = (self.sub - 1) % 100
end

local Main = class()
function Main:_init()
  self.clock = Clock:new()
  self.sub = Sub()
  self.value = 0
  Zephyr:advertise(self.clock, self)
end
function Main:run()
  while self.clock:tick() do
    self.sub:update()
    self.value = math.cos(self.clock:timeStep())
    -- print("Hi " .. self.value)  
  end  
end


Main():run()