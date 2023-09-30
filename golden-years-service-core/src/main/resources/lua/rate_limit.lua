local c
c = redis.call('get',KEYS[1])
if c and tonumber(c) > tonumber(ARGV[1]) then
    return c;
end
c = redis.call('incr',KEYS[1])
if tonumber(c) == 1 then
    redis.call('expire',KEYS[1],ARGV[2])
end
return c;
