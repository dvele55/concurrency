defmodule Lock do
  def loop(state) do
    receive do
      {:lock, sender} ->
        case state do
          [] ->
            send sender, :locked
            loop([sender])
          _ ->
              loop(state ++ [sender]) 
        end
      {:unlock, sender} ->
        case state do
          [] ->
            loop(state)
          [^sender | []] ->
            loop([])
          [^sender | [next | tail]] ->
            send next, :locked
            loop([next | tail])
          _ ->
            loop(state)
        end
    end
  end

  def lock(pid) do
    send pid, {:lock, self}
    receive do
      :locked -> nil # This will block until we receive message
    end
  end

  def unlock(pid) do
    send pid, {:unlock, self}
  end

  def locking(first, second, times) do
    if times > 0 do
      lock(first)
      lock(second)
      unlock(second)
      unlock(first)
      locking(first, second, times - 1)
    end
  end
end

a_lock = spawn fn -> Lock.loop([]) end
b_lock = spawn fn -> Lock.loop([]) end

this = self
IO.puts "Locking A, B started"
spawn fn ->
  Lock.locking(a_lock, b_lock, 1_000)
  IO.puts "Locking A, B finished"
  send this, :done
end
IO.puts "Locking A, B started"
spawn fn ->
  Lock.locking(a_lock, b_lock, 1_000)
  IO.puts "Locking A, B finished"
  send this, :done
end

IO.puts "Waiting for locking to be done"
receive do
  :done -> nil
end
receive do
  :done -> nil
end
