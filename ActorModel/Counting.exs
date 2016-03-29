defmodule Counting do
  def counter(value) do
    receive do
      {:get, sender} ->
        send sender, {:counter, value}
        counter value
      {:set, new_value} -> counter(new_value)
    end
  end

 def counting(sender, counter, times) do
    if times > 0 do
      send counter, {:get, self}
      receive do
        {:counter, value} -> send counter, {:set, value + 1}
      end
      counting(sender, counter, times - 1)
    else
      send sender, {:done, self}
    end
  end
end

counter = spawn fn -> Counting.counter 0 end

IO.puts "Starting counting processes"
this = self
counting1 = spawn fn ->
  IO.puts "Counting A started"
  Counting.counting this, counter, 500_000
  IO.puts "Counting A finished"
end
counting2 = spawn fn ->
  IO.puts "Counting B started"
  Counting.counting this, counter, 500_000
  IO.puts "Counting B finished"
end

IO.puts "Waiting for counting to be done"
receive do
  {:done, ^counting1} -> nil
end
receive do
  {:done, ^counting2} -> nil
end

send counter, {:get, self}
receive do
  {:counter, value} -> IO.puts "Counter is: #{value}"
end
