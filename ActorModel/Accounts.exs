defmodule Accounts do
  def accounts(state) do
    receive do
      {:transfer, source, destination, amount} ->
        accounts %{state | source => state[source] - amount , destination => state[destination] + amount}
      {:amounts, accounts, sender } ->
        send sender, {:amounts, for account <- accounts do
                        {account, state[account]}
                     end}
        accounts(state)
    end
  end

  def transfer(sender, accounts, source, destination, amount, times, inconsistencies) do
    if times > 0 do
      send accounts, {:amounts, [source, destination], self}
      receive do
        {:amounts, amounts} ->
          if amounts[source] + amounts[destination] != 500_000 do
            Agent.update(inconsistencies, fn value -> value + 1 end)
          end
      end
      send accounts, {:transfer, source, destination, amount}
      transfer(sender, accounts, source, destination, amount, times - 1, inconsistencies)
    else
      send sender, {:done, self}
    end
  end
end

accounts = spawn fn -> Accounts.accounts(%{bob: 200_000, joe: 300_000 }) end
{:ok, inconsistencies} = Agent.start(fn -> 0 end)
this = self
transfer1 = spawn fn ->
  IO.puts "Transfer A started"
  Accounts.transfer(this, accounts, :bob, :joe, 2, 100_000, inconsistencies)
  IO.puts "Transfer A finished"
end
transfer2 = spawn fn ->
  IO.puts "Transfer B started"
  Accounts.transfer(this, accounts, :joe, :bob, 1, 100_000, inconsistencies)
  IO.puts "Transfer B finished"
end

IO.puts "Waiting for transfers to be done"
receive do
  {:done, ^transfer1} -> nil
end
receive do
  {:done, ^transfer2} -> nil
end

send accounts, {:amounts, [:bob, :joe], self}
receive do
  {:amounts, amounts} ->
    IO.puts "Bob has in account: #{amounts[:bob]}"
    IO.puts "Joe has in account: #{amounts[:joe]}"
    IO.puts "Inconsistencies while transfer: #{Agent.get(inconsistencies, fn x -> x end)}"
end 
